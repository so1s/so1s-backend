package io.so1s.backend.domain.resource.dto.mapper;

import io.so1s.backend.domain.resource.dto.request.ResourceCreateRequestDto;
import io.so1s.backend.domain.resource.dto.response.ResourceFindResponseDto;
import io.so1s.backend.domain.resource.entity.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

  public ResourceFindResponseDto toDto(Resource entity) {
    return ResourceFindResponseDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .cpu(entity.getCpu())
        .gpu(entity.getGpu())
        .memory(entity.getMemory())
        .cpuLimit(entity.getCpuLimit())
        .gpuLimit(entity.getGpuLimit())
        .memoryLimit(entity.getMemoryLimit())
        .build();
  }

  public Resource toEntity(ResourceCreateRequestDto dto) {
    return Resource.builder()
        .name(dto.getName())
        .cpu(dto.getCpu())
        .memory(dto.getMemory())
        .gpu(dto.getGpu())
        .cpuLimit(dto.getCpuLimit())
        .memoryLimit(dto.getMemoryLimit())
        .gpuLimit(dto.getGpuLimit())
        .build();
  }

}
