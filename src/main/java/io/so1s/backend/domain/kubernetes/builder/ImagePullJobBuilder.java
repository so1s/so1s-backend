package io.so1s.backend.domain.kubernetes.builder;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.so1s.backend.domain.kubernetes.dto.ImageAuthDto;
import io.so1s.backend.domain.kubernetes.service.RegistryServiceImpl;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImagePullJobBuilder {

  private final RegistryServiceImpl registryService;

  public Job buildImagePullJob(ModelUploadRequestDto modelUploadRequestDto) {
    ImageAuthDto defaultImageAuthDto = ImageAuthDto.builder().build();

    return buildImagePullJob(modelUploadRequestDto, defaultImageAuthDto);
  }

  public Job buildImagePullJob(ModelUploadRequestDto modelUploadRequestDto,
      ImageAuthDto imageAuthDto)
      throws IllegalStateException {

    return new JobBuilder()
        .withApiVersion("batch/v1")
        .withNewMetadata()
        .withName("image-exporter")
        .addToLabels("job-name", "image-exporter")
        .endMetadata()
        .withNewSpec()
        .withNewTemplate()
        .withNewSpec()
        .addNewContainer()
        .withName("image-exporter")
        .withImage("docker:dind-rootless")
        .withCommand("/bin/sh", "-c", "\"",
            String.format("docker login -u %s -p %s && docker pull %s",
                modelUploadRequestDto.getUserId(), modelUploadRequestDto.getUserPassword(),
                modelUploadRequestDto.getUrl()), "\"")
        .endContainer()
        .withRestartPolicy("Never")
        .endSpec()
        .endTemplate()
        .endSpec()
        .build();
  }
}
