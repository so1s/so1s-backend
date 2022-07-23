package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.aws.service.FileSaveResultForm;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;

public interface ModelService {

  void validateDuplicateModelName(String name);

  Model createModel(ModelUploadRequestDto modelUploadRequestDto);

  ModelMetadata createModelMetadata(Model model, ModelUploadRequestDto modelUploadRequestDto,
      FileSaveResultForm fileSaveResultForm);

}
