package io.so1s.backend.domain.registry.service;

import io.so1s.backend.domain.registry.dto.request.RegistryUploadRequestDto;
import io.so1s.backend.domain.registry.entity.Registry;
import java.util.List;
import java.util.Optional;

public interface RegistryService {

  List<Registry> findAll();

  Registry saveRegistry(RegistryUploadRequestDto requestDto);

  Optional<Registry> findRegistryById(Long id);

}
