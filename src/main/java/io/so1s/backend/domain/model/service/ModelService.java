package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;

public interface ModelService {

  Model createModel(ModelUploadRequestDto modelUploadRequestDto);

  ModelMetadata createModelMetadata(Model model, ModelUploadRequestDto modelUploadRequestDto);

  ModelUploadResponseDto buildModel(ModelMetadata modelMetadata);

}
