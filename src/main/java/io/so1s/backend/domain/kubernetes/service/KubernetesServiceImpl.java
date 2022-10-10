package io.so1s.backend.domain.kubernetes.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.istio.api.networking.v1beta1.Gateway;
import io.fabric8.istio.api.networking.v1beta1.GatewayBuilder;
import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.api.networking.v1beta1.VirtualServiceBuilder;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.HostPathVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceQuotaBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.TolerationBuilder;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import io.so1s.backend.domain.kubernetes.exception.TooManyBuildRequestException;
import io.so1s.backend.domain.kubernetes.utils.JobStatusChecker;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.test.entity.ABTest;
import io.so1s.backend.global.utils.HashGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KubernetesServiceImpl implements KubernetesService {

  private final KubernetesClient client;
  private final IstioClient istioClient;
  private final JobStatusChecker jobStatusChecker;


  public String getWorkloadToYaml(HasMetadata object) {
    try {
      return SerializationUtils.dumpAsYaml(object);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Fail");
    }
  }

  @Override
  public boolean inferenceServerBuild(ModelMetadata modelMetadata) throws InterruptedException {
    Model model = modelMetadata.getModel();

    String namespace = "default";

    String tag = HashGenerator.sha256();
    String jobName = "build-" + tag.substring(0, 12).toLowerCase();
    String modelName = model.getName().toLowerCase();
    String library = model.getLibrary().getName().toLowerCase();
    String version = modelMetadata.getVersion().toLowerCase();

    Map<String, String> labels = new HashMap<>();
    labels.put("app", "inference-build");
    labels.put("name", jobName);
    labels.put("modelName", modelName);
    labels.put("version", version);

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
        .addNewContainer()
        .withImagePullPolicy("Always")
        .withName(jobName)
        .withImage("so1s/build:latest")
        .withCommand(
            "/bin/bash", "/apps/build.sh",
            "--file", modelMetadata.getUrl(),
            "--input", modelMetadata.getInputDtype(),
            "--output", modelMetadata.getOutputDtype(),
            "--name", modelName,
            "--tag", version,
            "--user", "so1s",
            "--password", "vkxmxkdlaj"
        )
        .withNewResources()
        .addToRequests("cpu", new Quantity("1"))
        .addToLimits("cpu", new Quantity("1"))
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
            .withValue("inference")
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
            .withName(name)
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
  public boolean deployInferenceServer(
      io.so1s.backend.domain.deployment.entity.Deployment deployment) {

    String namespace = "default";
    String deployName = deployment.getName().toLowerCase();
    String modelName = deployment.getModelMetadata().getModel().getName().toLowerCase();
    String modelVersion = deployment.getModelMetadata().getVersion().toLowerCase();

    Map<String, String> labels = new HashMap<>();
    labels.put("app", "inference");
    labels.put("name", deployName);

    String host = deployment.getEndPoint().toLowerCase();

    Deployment inferenceDeployment = new DeploymentBuilder()
        .withNewMetadata()
        .withName(deployName)
        .withNamespace(namespace)
        .addToLabels(labels)
        .endMetadata()
        .withNewSpec()
        .withReplicas(1)
        .withNewSelector()
        .addToMatchLabels(labels)
        .endSelector()
        .withNewTemplate()
        .withNewMetadata()
        .withName(deployName)
        .addToLabels(labels)
        .endMetadata()
        .withNewSpec()
        .addNewContainer()
        .withImagePullPolicy("Always")
        .withName(deployName)
        .withImage("so1s/" + modelName + ":" + modelVersion)
        .withNewResources()
        .addToRequests("cpu", new Quantity(deployment.getResource().getCpu()))
        .addToRequests("memory", new Quantity(deployment.getResource().getMemory()))
        .addToLimits("cpu", new Quantity(deployment.getResource().getCpuLimit()))
        .addToLimits("memory", new Quantity(deployment.getResource().getMemoryLimit()))
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

    // See https://github.com/So1S/istio/blob/main/gateway.yaml

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

    return true;
  }

  @Transactional(readOnly = true)
  @Override
  public boolean deployABTest(ABTest abTest) {

    String namespace = "default";
    String abTestName = "ab-test-" + abTest.getName().toLowerCase();

    String host = abTestName + ".so1s.io"; // TODO: Fix hard-coded root domain

    String aName = abTest.getA().getName().toLowerCase();
    String bName = abTest.getB().getName().toLowerCase();

    Map<String, String> labels = new HashMap<>();
    labels.put("app", "ab-test");
    labels.put("name", abTestName);

    VirtualService abTestVirtualService = new VirtualServiceBuilder()
        .withNewMetadata()
        .withName(abTestName)
        .withNamespace(namespace)
        .addToLabels(labels)
        .endMetadata()
        .withNewSpec()
        .withHosts(host)
        .withGateways(abTestName)
        .addNewHttp()
        .addNewMatch()
        .withNewUri()
        .withNewStringMatchPrefixType("/")
        .endUri()
        .endMatch()
        .addNewRoute()
        .withWeight(50)
        .withNewDestination()
        .withHost(aName)
        .withNewPort()
        .withNumber(3000)
        .endPort()
        .endDestination()
        .endRoute()
        .addNewRoute()
        .withWeight(50)
        .withNewDestination()
        .withHost(bName)
        .withNewPort()
        .withNumber(3000)
        .endPort()
        .endDestination()
        .endRoute()
        .endHttp()
        .endSpec()
        .build();

    Gateway abTestGateway = new GatewayBuilder()
        .withNewMetadata()
        .withName(abTestName)
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

    try {
      istioClient.v1beta1().gateways().inNamespace(namespace).createOrReplace(abTestGateway);
      istioClient.v1beta1().virtualServices().inNamespace(namespace)
          .createOrReplace(abTestVirtualService);
    } catch (KubernetesClientException ignored) {
      return false;
    }

    return true;
  }

  @Override
  public boolean deleteDeployment(io.so1s.backend.domain.deployment.entity.Deployment deployment) {
    String namespace = "default";
    String deploymentName = deployment.getName().toLowerCase();

    try {
      client.apps().deployments().inNamespace(namespace).withName(deploymentName).delete();
      client.services().inNamespace(namespace).withName(deploymentName).delete();
      istioClient.v1beta1().gateways().inNamespace(namespace).withName(deploymentName).delete();
      istioClient.v1beta1().virtualServices().inNamespace(namespace).withName(deploymentName)
          .delete();
    } catch (KubernetesClientException ignored) {
      return false;
    }

    return true;
  }

  @Override
  public boolean deleteABTest(ABTest abTest) {
    String namespace = "default";
    String abTestName = "ab-test-" + abTest.getName().toLowerCase();

    try {
      istioClient.v1beta1().gateways().inNamespace(namespace).withName(abTestName).delete();
      istioClient.v1beta1().virtualServices().inNamespace(namespace).withName(abTestName).delete();
    } catch (KubernetesClientException ignored) {
      return false;
    }

    return true;
  }

  public HasMetadata getDeploymentObject(String name) {
    List<Deployment> deployments = client.apps().deployments().inNamespace("default")
        .withLabel("app", "inference").list()
        .getItems();

    return deployments.stream().filter((item) -> item.getMetadata().getName().equals(name))
        .collect(Collectors.toList())
        .get(deployments.size() - 1);
  }

  public HasMetadata getJobObject(String name) {
    List<Job> jobs = client.batch().v1().jobs().inNamespace("default")
        .withLabel("app", "inference-build").list()
        .getItems();

    return jobs.stream()
        .filter((item) -> item.getMetadata().getLabels().get("modelName").equals(name))
        .collect(Collectors.toList())
        .get(jobs.size() - 1);
  }
}
