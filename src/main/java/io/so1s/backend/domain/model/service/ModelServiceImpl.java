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
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

  private final ModelRepository modelRepository;
  private final ModelMetadataRepository modelMetadataRepository;
  private final KubernetesService kubernetesService;

  @Transactional
  public ModelUploadResponseDto upload(ModelUploadRequestDto modelUploadRequestDto) {
    Model saveModel = modelRepository.save(modelUploadRequestDto.toEntity());
    String version = HashGenerator.sha256();
    ModelMetadata saveModelMetadata = modelMetadataRepository.save(
        ModelMetadata.builder()
            .model(saveModel)
            .url(modelUploadRequestDto.getUrl())
            .version(version)
            .info(modelUploadRequestDto.getInfo())
            .status("pending")
            .build());

    // kubernetes job run
    kubernetesService.inferenceServerBuild(saveModel, version);

    return ModelUploadResponseDto.builder()
        .success(Boolean.TRUE)
        .name(saveModel.getName())
        .version(saveModelMetadata.getVersion())
        .build();
  }
}
