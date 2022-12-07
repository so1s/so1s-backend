package io.so1s.backend.domain.registry.dto.mapper;

import io.so1s.backend.domain.registry.dto.request.RegistryUploadRequestDto;
import io.so1s.backend.domain.registry.dto.response.RegistryFindResponseDto;
import io.so1s.backend.domain.registry.entity.Registry;

public interface RegistryMapper {

  Registry toEntity(RegistryUploadRequestDto dto);

  RegistryFindResponseDto toDto(Registry entity);

  String toStringFormat(Registry registry);

}
