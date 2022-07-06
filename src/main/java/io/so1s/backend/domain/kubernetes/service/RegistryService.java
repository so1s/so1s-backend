package io.so1s.backend.domain.kubernetes.service;

import io.so1s.backend.domain.kubernetes.dto.ImageAuthPolicy;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;

public interface RegistryService {

  ImageAuthPolicy findAuthPolicy(ModelUploadRequestDto modelUploadRequestDto);

}
