package io.so1s.backend.domain.kubernetes.builder;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.so1s.backend.domain.kubernetes.dto.ImageAuthDto;
import io.so1s.backend.domain.kubernetes.service.RegistryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImagePullJobBuilder {

  private final RegistryServiceImpl registryService;

  public Job buildImagePullJob(String image) {
    ImageAuthDto defaultImageAuthDto = ImageAuthDto.builder().build();

    return buildImagePullJob(image, defaultImageAuthDto);
  }

  public Job buildImagePullJob(String image, ImageAuthDto imageAuthDto)
      throws IllegalStateException {

    switch (imageAuthDto.getAuthPolicy()) {
      case DOCKER_HUB:
        if (imageAuthDto.getApikey().isBlank()) {

        }
        break;
      case AWS_PRIVATE:
        if (!imageAuthDto.getApikey().isBlank()) {

        } else if (!imageAuthDto.getUserName().isBlank() && !imageAuthDto.getPassword().isBlank()) {

        } else {

        }
        break;
      case AWS_PUBLIC:
      case NONE:
    }

    return new JobBuilder()
        .withApiVersion("batch/v1")
        .withNewMetadata()
        .withName("image-exporter")
        .endMetadata()
        .withNewSpec()
        .withNewTemplate()
        .withNewSpec()
        .addNewContainer()
        .withName("image-exporter")
        .withImage("docker:dind-rootless")
        .withCommand("/bin/sh", "-c", "\"", "", "", "\"")
        .endContainer()
        .withRestartPolicy("Never")
        .endSpec()
        .endTemplate()
        .endSpec()
        .build();
  }
}
