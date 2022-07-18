package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.global.utils.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

  private final ModelRepository modelRepository;
  private final ModelMetadataRepository modelMetadataRepository;
  private final KubernetesService kubernetesService;

  public Model createModel(ModelUploadRequestDto modelUploadRequestDto) {
    return modelRepository.save(modelUploadRequestDto.toEntity());
  }

  public ModelMetadata createModelMetadata(Model model,
      ModelUploadRequestDto modelUploadRequestDto) {
    return modelMetadataRepository.save(ModelMetadata.builder()
        .model(model)
        .url(modelUploadRequestDto.getUrl())
        .version(HashGenerator.sha1())
        .info(modelUploadRequestDto.getInfo())
        .status("pending")
        .build());
  }

  public ModelUploadResponseDto buildModel(ModelMetadata modelMetadata) {
    return ModelUploadResponseDto.builder()
        .success(kubernetesService.inferenceServerBuild(modelMetadata))
        .name(modelMetadata.getModel().getName())
        .version(modelMetadata.getVersion())
        .build();
  }

}
