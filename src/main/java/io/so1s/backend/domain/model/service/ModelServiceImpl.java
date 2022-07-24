package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.global.error.exception.DuplicateModelNameException;
import io.so1s.backend.global.utils.HashGenerator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

  private final ModelRepository modelRepository;
  private final ModelMetadataRepository modelMetadataRepository;

  @Transactional(readOnly = true)
  public void validateDuplicateModelName(String name) {
    Optional<Model> result = modelRepository.findByName(name);
    if (result.isPresent()) {
      throw new DuplicateModelNameException(
          String.format("중복된 모델명이 있습니다. (이름 : %s, 생성시간 : %s)",
              result.get().getName(),
              result.get().getCreatedOn()));
    }
  }

  @Transactional
  public Model createModel(ModelUploadRequestDto modelUploadRequestDto) {
    validateDuplicateModelName(modelUploadRequestDto.getName());
    return modelRepository.save(modelUploadRequestDto.toModelEntity());
  }

  @Transactional
  public ModelMetadata createModelMetadata(Model model,
      ModelUploadRequestDto modelUploadRequestDto, FileSaveResultForm fileSaveResultForm) {
    return modelMetadataRepository.save(ModelMetadata.builder()
        .status("pending")
        .version(HashGenerator.sha256())
        .fileName(fileSaveResultForm.getSavedName())
        .url(fileSaveResultForm.getUrl())
        .inputShape(modelUploadRequestDto.getInputShape())
        .inputDtype(modelUploadRequestDto.getInputDtype())
        .outputShape(modelUploadRequestDto.getOutputShape())
        .outputDtype(modelUploadRequestDto.getOutputDtype())
        .model(model)
        .build());
  }

}
