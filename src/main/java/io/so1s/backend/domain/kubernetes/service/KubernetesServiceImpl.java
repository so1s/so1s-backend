package io.so1s.backend.domain.kubernetes.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.istio.api.networking.v1beta1.Gateway;
import io.fabric8.istio.api.networking.v1beta1.GatewayBuilder;
import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.api.networking.v1beta1.VirtualServiceBuilder;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.HostPathVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.LocalObjectReference;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceQuotaBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.TolerationBuilder;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.autoscaling.v2beta2.HorizontalPodAutoscaler;
import io.fabric8.kubernetes.api.model.autoscaling.v2beta2.HorizontalPodAutoscalerBuilder;
import io.fabric8.kubernetes.api.model.autoscaling.v2beta2.MetricSpec;
import io.fabric8.kubernetes.api.model.autoscaling.v2beta2.MetricSpecBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import io.so1s.backend.domain.auth.service.UserService;
import io.so1s.backend.domain.deployment.dto.request.Standard;
import io.so1s.backend.domain.kubernetes.exception.TooManyBuildRequestException;
import io.so1s.backend.domain.kubernetes.utils.JobStatusChecker;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.registry.entity.Registry;
import io.so1s.backend.domain.registry.service.RegistryKubernetesService;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.global.utils.HashGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class KubernetesServiceImpl implements KubernetesService {

  private final KubernetesClient client;
  private final IstioClient istioClient;
  private final JobStatusChecker jobStatusChecker;
  private final RegistryKubernetesService registryKubernetesService;
  private final NamespaceService namespaceService;
  private final UserService userService;
  private final TextEncryptor textEncryptor;


  public String getWorkloadToYaml(HasMetadata object) {
    try {
      return SerializationUtils.dumpAsYaml(object);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Yaml dump failed.");
    }
  }

  @Override
  @Transactional(readOnly = true)
  public boolean inferenceServerBuild(ModelMetadata modelMetadata) throws InterruptedException {
    String namespace = namespaceService.getNamespace();
    Model model = modelMetadata.getModel();
    Registry registry = modelMetadata.getRegistry();

    String tag = HashGenerator.sha256().toLowerCase();
    String jobName = "build-" + tag.substring(0, 12).toLowerCase();
    String modelName = model.getName().toLowerCase();
    String library = model.getLibrary().getName().toLowerCase();
    String version = modelMetadata.getVersion().toLowerCase();
    String type = modelMetadata.getDeviceType().toLowerCase();

    Map<String, String> labels = new HashMap<>();
    labels.put("app", "inference-build");
    labels.put("name", jobName);
    labels.put("modelName", modelName);
    labels.put("version", version);

    createNamespace(userService.getCurrentUsername());

    final Job job = new JobBuilder()
        .withApiVersion("batch/v1")
        .withNewMetadata()
        .withName(jobName)
        .withNamespace(namespace)
        .addToLabels(labels)
        .endMetadata()
        .withNewSpec()
        .withNewTemplate()
        .withNewMetadata()
        .addToAnnotations("sidecar.istio.io/inject", "false")
        .endMetadata()
        .withNewSpec()
        .withSchedulerName(
            (type.equals("gpu") ? "gpu-resource-scheduler"
                : "default-scheduler"))
        .addNewContainer()
        .withImagePullPolicy("Always")
        .withName(jobName)
        .withImage("ghcr.io/so1s/model-template:" + type)
        .withCommand(
            "/bin/bash", "/apps/build.sh",
            "--file", modelMetadata.getUrl(),
            "--input", modelMetadata.getInputDtype(),
            "--output", modelMetadata.getOutputDtype(),
            "--name", modelName,
            "--tag", version,
            "--library", library,
            "--registry", registry.getBaseUrl(),
            "--user", registry.getUsername(),
            "--password", textEncryptor.decrypt(registry.getPassword()),
            "--type", type
        )
        .withNewResources()
        .addToRequests("cpu", new Quantity("1"))
        .addToLimits("cpu", new Quantity("1"))
        .addToRequests("nvidia.com/gpu", new Quantity(type.equals("gpu") ? "1" : "0"))
        .addToLimits("nvidia.com/gpu", new Quantity(type.equals("gpu") ? "1" : "0"))
        .endResources()
        .withVolumeMounts(
            new VolumeMountBuilder()
                .withMountPath("/var/run/docker.sock")
                .withName("docker-sock")
                .build())
        .endContainer()
        .withTolerations(new TolerationBuilder()
            .withKey("kind")
            .withOperator("Equal")
            .withValue("model-builder")
            .withEffect("NoSchedule")
            .build())
        .withVolumes(new VolumeBuilder()
            .withName("docker-sock")
            .withHostPath(new HostPathVolumeSourceBuilder()
                .withPath("/var/run/docker.sock")
                .build())
            .build())
        .withRestartPolicy("Never")
        .endSpec()
        .endTemplate()
        .withBackoffLimit(2)
        .endSpec()
        .build();

    client.batch().v1().jobs().inNamespace(namespace).createOrReplace(job);

    try {
      jobStatusChecker.checkJobStatus(jobName, namespace, modelMetadata);
    } catch (TaskRejectedException e) { // QueueCapacity 초과 요청 방어 코드 작성
      throw new TooManyBuildRequestException(
          "Too many jobs are currently running. Please run it after the other work is completed.");
    }

    return true;
  }

  @Override
  public boolean createNamespace(String name) {
    client.namespaces().createOrReplace(
        new NamespaceBuilder()
            .withNewMetadata()
            .addToLabels(Map.of("istio-injection", "enabled"))
            .withName("so1s-" + name)
            .endMetadata()
            .build());

    return true;
  }

  @Override
  public boolean createResourceQuota(Resource resource, String namespace) {
    Map<String, Quantity> resources = new HashMap<>();
    resources.put("requests.cpu", new Quantity(resource.getCpu()));
    resources.put("requests.memory", new Quantity(resource.getMemory()));

    client.resourceQuotas().inNamespace(namespace)
        .createOrReplace(new ResourceQuotaBuilder()
            .withNewMetadata()
            .withName(namespace + "-resource-quota")
            .endMetadata()
            .withNewSpec()
            .addToHard(resources)
            .endSpec()
            .build());

    return true;
  }

  @Override
  public boolean createResourceQuotaWithGpu(Resource resource, String namespace) {
    Map<String, Quantity> resources = new HashMap<>();
    resources.put("requests.cpu", new Quantity(resource.getCpu()));
    resources.put("requests.memory", new Quantity(resource.getMemory()));
    resources.put("requests.nvidia.com/gpu", new Quantity(resource.getGpu()));

    client.resourceQuotas().inNamespace(namespace)
        .createOrReplace(new ResourceQuotaBuilder()
            .withNewMetadata()
            .withName(namespace + "-resource-quota-with-gpu")
            .endMetadata()
            .withNewSpec()
            .addToHard(resources)
            .endSpec()
            .build());

    return true;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean deployInferenceServer(
      io.so1s.backend.domain.deployment.entity.Deployment deployment) {
    String namespace = namespaceService.getNamespace();
    String deployName = "inference-" + deployment.getName().toLowerCase();

    var modelMetadata = deployment.getModelMetadata();
    var registry = modelMetadata.getRegistry();

    String registryUrl = registry.getBaseUrl();
    String registryName = registry.getName();
    String registryUser = registry.getUsername();
    String modelName = modelMetadata.getModel().getName().toLowerCase();
    String modelVersion = modelMetadata.getVersion().toLowerCase();

    createNamespace(userService.getCurrentUsername());
    registryKubernetesService.deployRegistrySecret(registry);

    Map<String, String> labels = new HashMap<>();
    labels.put("app", "inference");
    labels.put("name", deployName);

    Map<String, String> annotations = new HashMap<>();
    annotations.put("sidecar.istio.io/inject", "true");

    String host = deployment.getEndPoint().toLowerCase();

    Deployment inferenceDeployment = new DeploymentBuilder()
        .withNewMetadata()
        .withName(deployName)
        .withNamespace(namespace)
        .addToLabels(labels)
        .endMetadata()
        .withNewSpec()
        .withReplicas(
            deployment.getStandard() == Standard.REPLICAS ? deployment.getStandardValue() : 1)
        .withNewSelector()
        .addToMatchLabels(labels)
        .endSelector()
        .withNewTemplate()
        .withNewMetadata()
        .withName(deployName)
        .addToLabels(labels)
        .addToAnnotations(annotations)
        .endMetadata()
        .withNewSpec()
        .withSchedulerName(
            (!deployment.getResource().getGpu().equals("0")) ? "gpu-resource-scheduler"
                : "default-scheduler")
        .withImagePullSecrets(new LocalObjectReference(registryName))
        .addNewContainer()
        .withImagePullPolicy("Always")
        .withName(deployName)
        .withImage(String.format("%s/%s/%s:%s", registryUrl, registryUser, modelName, modelVersion))
        .withNewResources()
        .addToRequests("cpu", new Quantity(deployment.getResource().getCpu()))
        .addToRequests("memory", new Quantity(deployment.getResource().getMemory()))
        .addToRequests("nvidia.com/gpu", new Quantity(deployment.getResource().getGpu()))
        .addToLimits("cpu", new Quantity(deployment.getResource().getCpuLimit()))
        .addToLimits("memory", new Quantity(deployment.getResource().getMemoryLimit()))
        .addToLimits("nvidia.com/gpu", new Quantity(deployment.getResource().getGpuLimit()))
        .endResources()
        .addNewPort()
        .withName("inference-port")
        .withContainerPort(3000)
        .endPort()
        .endContainer()
        .withTolerations(new TolerationBuilder()
            .withKey("kind")
            .withOperator("Equal")
            .withValue("inference")
            .withEffect("NoSchedule")
            .build())
        .endSpec()
        .endTemplate()
        .endSpec()
        .build();

    io.fabric8.kubernetes.api.model.Service inferenceService = new ServiceBuilder()
        .withNewMetadata()
        .withName(deployName)
        .withNamespace(namespace)
        .addToLabels(labels)
        .endMetadata()
        .withNewSpec()
        .withSelector(labels)
        .addNewPort()
        .withName("inference-port")
        .withPort(3000)
        .withProtocol("TCP")
        .endPort()
        .endSpec()
        .build();

    Gateway inferenceGateway = new GatewayBuilder()
        .withNewMetadata()
        .withName(deployName)
        .withNamespace(namespace)
        .addToLabels(labels)
        .endMetadata()
        .withNewSpec()
        .addNewServer()
        .withNewPort()
        .withNumber(80)
        .withName("http")
        .withProtocol("HTTP")
        .endPort()
        .withHosts(host)
        .endServer()
        .addNewServer()
        .withNewPort()
        .withNumber(9443)
        .withName("http-dev")
        .withProtocol("HTTP")
        .endPort()
        .withHosts(host)
        .endServer()
        .endSpec()
        .build();

    VirtualService inferenceVirtualService = new VirtualServiceBuilder()
        .withNewMetadata()
        .withName(deployName)
        .withNamespace(namespace)
        .addToLabels(labels)
        .endMetadata()
        .withNewSpec()
        .withHosts(host)
        .withGateways(deployName)
        .addNewHttp()
        .addNewMatch()
        .withNewUri()
        .withNewStringMatchPrefixType("/")
        .endUri()
        .endMatch()
        .addNewRoute()
        .withNewDestination()
        .withNewPort()
        .withNumber(3000)
        .endPort()
        .withHost(deployName)
        .endDestination()
        .endRoute()
        .endHttp()
        .endSpec()
        .build();

    client.apps().deployments().inNamespace(namespace).createOrReplace(inferenceDeployment);
    client.services().inNamespace(namespace).createOrReplace(inferenceService);
    istioClient.v1beta1().gateways().inNamespace(namespace).createOrReplace(inferenceGateway);
    istioClient.v1beta1().virtualServices().inNamespace(namespace)
        .createOrReplace(inferenceVirtualService);

    if (deployment.getStandard() != Standard.REPLICAS) {
      createHPA(deployment, namespace);
    }

    return true;
  }


  @Override
  public boolean deleteInferenceServer(
      io.so1s.backend.domain.deployment.entity.Deployment deployment) {
    String namespace = namespaceService.getNamespace();
    String deploymentName = "inference-" + deployment.getName().toLowerCase();

    try {
      client.apps().deployments().inNamespace(namespace).withName(deploymentName).delete();
      client.services().inNamespace(namespace).withName(deploymentName).delete();
      istioClient.v1beta1().gateways().inNamespace(namespace).withName(deploymentName).delete();
      istioClient.v1beta1().virtualServices().inNamespace(namespace).withName(deploymentName)
          .delete();
      client.autoscaling().v2beta2().horizontalPodAutoscalers().inNamespace(namespace)
          .withName(deploymentName + "-hpa")
          .delete();
    } catch (KubernetesClientException ignored) {
      return false;
    }

    return true;
  }


  public HasMetadata getDeploymentObject(String name) {
    String namespace = namespaceService.getNamespace();
    List<Deployment> deployments = client.apps().deployments()
        .inNamespace(namespace)
        .withLabel("app", "inference").list()
        .getItems();

    return deployments.stream().filter((item) -> item.getMetadata().getName().equals(name))
        .findFirst().get();
  }

  public HasMetadata getJobObject(String name) {
    String namespace = namespaceService.getNamespace();
    List<Job> jobs = client.batch().v1().jobs().inNamespace(namespace)
        .withLabel("app", "inference-build").list()
        .getItems();

    return jobs.stream()
        .filter((item) -> item.getMetadata().getLabels().get("modelName").equals(name))
        .findFirst().get();
  }

  public boolean createHPA(io.so1s.backend.domain.deployment.entity.Deployment deployment,
      String namespace) {
    HorizontalPodAutoscaler horizontalPodAutoscaler = new HorizontalPodAutoscalerBuilder()
        .withNewMetadata().withName(deployment.getName().toLowerCase() + "-hpa")
        .withNamespace(namespace).endMetadata()
        .withNewSpec()
        .withNewScaleTargetRef()
        .withApiVersion("apps/v1")
        .withKind("Deployment")
        .withName("inference-" + deployment.getName().toLowerCase())
        .endScaleTargetRef()
        .withMinReplicas(deployment.getMinReplicas())
        .withMaxReplicas(deployment.getMaxReplicas())
        .addToMetrics(
            deployment.getStandard().name().equals("GPU") ? createGpuUtilizationMetricSpec(
                deployment) : createLatencyMetricSpec(deployment))
        .withNewBehavior()
        .withNewScaleDown()
        .addNewPolicy()
        .withType("Pods")
        .withValue(4)
        .withPeriodSeconds(60)
        .endPolicy()
        .addNewPolicy()
        .withType("Percent")
        .withValue(10)
        .withPeriodSeconds(60)
        .endPolicy()
        .endScaleDown()
        .endBehavior()
        .endSpec()
        .build();

    try {
      client.autoscaling().v2beta2().horizontalPodAutoscalers().inNamespace(namespace)
          .createOrReplace(horizontalPodAutoscaler);
    } catch (KubernetesClientException ignored) {
      return false;
    }

    return true;
  }

  private MetricSpec createLatencyMetricSpec(
      io.so1s.backend.domain.deployment.entity.Deployment deployment) {
    return new MetricSpecBuilder().withType("Object")
        .withNewObject()
        .withNewMetric()
        .withName("request_duration_milliseconds")
        .endMetric()
        .withNewDescribedObject()
        .withApiVersion("apps/v1")
        .withKind("Deployment")
        .withName("inference-" + deployment.getName().toLowerCase())
        .endDescribedObject()
        .withNewTarget()
        .withType(deployment.getStandard().getType())
        .withValue(
            new Quantity(deployment.getStandardValue() + deployment.getStandard().getUnit()))
        .endTarget()
        .endObject().build();
  }

  public MetricSpec createGpuUtilizationMetricSpec(
      io.so1s.backend.domain.deployment.entity.Deployment deployment) {
    return new MetricSpecBuilder().withType("Pods")
        .withNewPods()
        .withNewMetric()
        .withName("DCGM_FI_DEV_GPU_UTIL")
        .endMetric()
        .withNewTarget()
        .withType(deployment.getStandard().getType())
        .withAverageValue(new Quantity(String.valueOf(deployment.getStandardValue())))
        .endTarget()
        .endPods().build();
  }
}
