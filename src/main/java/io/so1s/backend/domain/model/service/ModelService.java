package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModelService {

  private final ModelRepository modelRepository;
  private final ModelMetadataRepository modelMetadataRepository;

  @Transactional
  public ModelUploadResponseDto save(ModelUploadRequestDto modelUploadRequestDto) {
    Model model = modelRepository.findByName(modelUploadRequestDto.getModelName());

    if (model == null) {
      model = Model.builder()
          .name(modelUploadRequestDto.getModelName())
          .build();
    }

    model = modelRepository.saveAndFlush(model);
    ModelMetadata metadata = ModelMetadata.builder()
        .url(modelUploadRequestDto.getUrl())
        .info(modelUploadRequestDto.getInfo())
        .version(modelUploadRequestDto.getVersion())
        .model(model)
        .build();

    modelMetadataRepository.saveAndFlush(metadata);

    return ModelUploadResponseDto.builder()
        .success(true)
        .modelName(model.getName())
        .build();
  }
}
