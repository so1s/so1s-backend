package io.so1s.backend.domain.kubernetes.service;

import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;

public interface ImageService {

  boolean checkImageExists(ModelUploadRequestDto modelUploadRequestDto);

  boolean checkAuthInfoNotGiven(ModelUploadRequestDto modelUploadRequestDto);

}
