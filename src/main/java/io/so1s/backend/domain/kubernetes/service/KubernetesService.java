package io.so1s.backend.domain.kubernetes.service;

import io.fabric8.kubernetes.api.model.HostPathVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.global.error.exception.FailedInferenceBuildException;
import io.so1s.backend.global.utils.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KubernetesService {

  private final KubernetesClient client;
  private final String namespace = "inference-build";

  public boolean inferenceServerBuild(Model model, String version)
      throws FailedInferenceBuildException {

    String tag = HashGenerator.hashGenerateBySha256();
    String jobName = (model.getName()
        + "-build-" + tag.substring(0, 6)).toLowerCase();
    String library = model.getLibrary().toLowerCase();

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
        .withImage("shinilseop12/"+library+"-build:v1")
        .withCommand("sudo", "usermod", "-a", "-G", "docker", "$USER")
        .withCommand("/bin/sh", "/apps/build.sh", model.getName().toLowerCase(), version)
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
