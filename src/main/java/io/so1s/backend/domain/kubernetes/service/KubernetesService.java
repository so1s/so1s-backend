package io.so1s.backend.domain.kubernetes.service;

import io.fabric8.kubernetes.api.model.HostPathVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceQuotaBuilder;
import io.fabric8.kubernetes.api.model.TolerationBuilder;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.so1s.backend.domain.deployment.entity.Resource;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.global.utils.HashGenerator;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KubernetesService {

  private final KubernetesClient client;

  public boolean inferenceServerBuild(ModelMetadata modelMetadata) {
    Model model = modelMetadata.getModel();

    String namespace = "default";

    String tag = HashGenerator.sha256();
    String jobName = (model.getName()
        + "-build-" + tag.substring(0, 6)).toLowerCase();
    String library = model.getLibrary().toLowerCase();
    String version = modelMetadata.getVersion().toLowerCase();

    final Job job = new JobBuilder()
        .withApiVersion("batch/v1")
        .withNewMetadata()
        .withName(jobName)
        .withNamespace(namespace)
        .addToLabels("job-name", jobName)
        .endMetadata()
        .withNewSpec()
        .withNewTemplate()
        .withNewSpec()
        .addNewContainer()
        .withName(jobName)
        .withImage("so1s/" + library + "-build:v1")
        .withCommand("/bin/sh", "/apps/build.sh", model.getName().toLowerCase(), version,
            "vkxmxkdlaj")
        .withNewResources()
        .addToRequests("cpu", new Quantity("1"))
        .addToRequests("memory", new Quantity("1Gi"))
        .addToLimits("cpu", new Quantity("1"))
        .addToLimits("memory", new Quantity("1Gi"))
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
        .withBackoffLimit(5)
        .endSpec()
        .build();

    client.batch().v1().jobs().inNamespace(namespace).createOrReplace(job);

    return true;
  }

  public boolean createNamespace(String name) {
    client.namespaces().createOrReplace(
        new NamespaceBuilder()
            .withNewMetadata()
            .withName(name)
            .endMetadata()
            .build());

    return true;
  }

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

  public boolean deployInferenceServer(
      io.so1s.backend.domain.deployment.entity.Deployment deployment) {

    String namespace = "default";
    String deployName = deployment.getName().toLowerCase();
    String modelName = deployment.getModelMetadata().getModel().getName().toLowerCase();
    String modelVersion = deployment.getModelMetadata().getVersion().toLowerCase();

    Map<String, String> labels = new HashMap<>();
    labels.put("inference", deployName);
    labels.put("model", modelName);
    labels.put("version", modelVersion);

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
        .withContainerPort(8080)
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

    client.apps().deployments().inNamespace(namespace).create(inferenceDeployment);

    return true;
  }
}
