package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;

public interface ModelService {

  ModelUploadResponseDto upload(ModelUploadRequestDto modelUploadRequestDto);

}
