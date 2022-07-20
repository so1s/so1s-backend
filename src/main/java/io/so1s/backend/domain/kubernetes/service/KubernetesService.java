package io.so1s.backend.domain.kubernetes.service;

import io.fabric8.kubernetes.api.model.HostPathVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.global.utils.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KubernetesService {

  private final KubernetesClient client;

  public boolean inferenceServerBuild(ModelMetadata modelMetadata) {
    Model model = modelMetadata.getModel();

    String namespace = "inference-build";

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
        .withVolumeMounts(
            new VolumeMountBuilder()
                .withMountPath("/var/run/docker.sock")
                .withName("docker-sock")
                .build())
        .endContainer()
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

}
