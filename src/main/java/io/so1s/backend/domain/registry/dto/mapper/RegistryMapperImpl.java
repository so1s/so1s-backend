package io.so1s.backend.domain.registry.dto.mapper;

import io.so1s.backend.domain.crypto.service.SecretKeyService;
import io.so1s.backend.domain.registry.dto.request.RegistryUploadRequestDto;
import io.so1s.backend.domain.registry.dto.response.RegistryFindResponseDto;
import io.so1s.backend.domain.registry.entity.Registry;
import io.so1s.backend.global.utils.Base64Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistryMapperImpl implements RegistryMapper {

  private final SecretKeyService secretKeyService;

  @Override
  public Registry toEntity(RegistryUploadRequestDto dto) {
    return Registry.builder()
        .name(dto.getName())
        .username(dto.getUsername())
        .password(secretKeyService.encode(dto.getPassword()))
        .baseUrl(dto.getBaseUrl())
        .build();
  }

  @Override
  public RegistryFindResponseDto toDto(Registry entity) {
    return RegistryFindResponseDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .username(entity.getUsername())
        .baseUrl(entity.getBaseUrl())
        .build();
  }

  @Override
  public String toStringFormat(Registry registry) {
    return String.format("%s / %s / %s", registry.getName(), registry.getUsername(),
        registry.getBaseUrl());
  }

  @Override
  public String toJsonEncoded(Registry registry) {
    String baseUrl = registry.getBaseUrl();
    String username = registry.getUsername();
    String password = secretKeyService.decode(registry.getPassword());

    String auth = Base64Mapper.encode(String.format("%s:%s", username, password));

    return Base64Mapper.encode(String.format(
        "{\"auths\":{\"%s\":{\"username\":\"%s\",\"password\":\"%s\",\"auth\":\"%s\"}}}",
        baseUrl,
        username,
        password,
        auth));
  }
}
