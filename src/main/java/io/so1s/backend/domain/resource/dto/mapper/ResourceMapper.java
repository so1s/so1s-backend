package io.so1s.backend.domain.resource.dto.mapper;

import io.so1s.backend.domain.resource.dto.response.ResourceFindResponseDto;
import io.so1s.backend.domain.resource.entity.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

  public ResourceFindResponseDto toDto(Resource entity) {
    return ResourceFindResponseDto.builder()
        .id(entity.getId())
        .cpu(entity.getCpu())
        .gpu(entity.getGpu())
        .memory(entity.getMemory())
        .cpuLimit(entity.getCpuLimit())
        .gpuLimit(entity.getGpuLimit())
        .memoryLimit(entity.getMemoryLimit())
        .build();
  }

}
