package io.so1s.backend.domain.kubernetes.service;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.so1s.backend.domain.kubernetes.builder.ImagePullJobBuilder;
import io.so1s.backend.domain.kubernetes.dto.ImageAuthDto;
import io.so1s.backend.domain.kubernetes.dto.ImageAuthPolicy;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  private final RegistryService registryService;
  private final ImagePullJobBuilder imagePullJobBuilder;
  private final KubernetesClient client;
  private final String kubernetesNamespace;

  @Override
  public boolean checkImageExists(ModelUploadRequestDto modelUploadRequestDto) {
    ImageAuthDto imageAuthDto = ImageAuthDto.builder()
        .userId(modelUploadRequestDto.getUserId())
        .userPassword(modelUploadRequestDto.getUserPassword())
        .authPolicy(registryService.findAuthPolicy(modelUploadRequestDto))
        .build();

    Job job = imagePullJobBuilder.buildImagePullJob(modelUploadRequestDto, imageAuthDto);

    client.batch().v1().jobs().inNamespace(kubernetesNamespace).createOrReplace(job);

    PodList podList = client.pods().inNamespace(kubernetesNamespace)
        .withLabel("job-name", job.getMetadata().getName()).list();

    return client.pods().inNamespace(kubernetesNamespace)
        .withName(podList.getItems().get(0).getMetadata().getName())
        .waitUntilCondition(pod -> pod.getStatus().getPhase().equals("Succeeded"), 3,
            TimeUnit.MINUTES) != null;
  }

  @Override
  public boolean checkAuthInfoNotGiven(ModelUploadRequestDto modelUploadRequestDto) {
    ImageAuthPolicy imageAuthPolicy = registryService.findAuthPolicy(
        modelUploadRequestDto);

    if (imageAuthPolicy == ImageAuthPolicy.DOCKERHUB_PUBLIC
        || imageAuthPolicy == ImageAuthPolicy.AWS_PUBLIC) {
      return false;
    }

    return modelUploadRequestDto.getUserId() == null
        || modelUploadRequestDto.getUserPassword() == null;
  }
}
