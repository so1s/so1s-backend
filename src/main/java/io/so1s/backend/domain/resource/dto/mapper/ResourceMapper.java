package io.so1s.backend.domain.resource.dto.mapper;

import io.fabric8.kubernetes.api.model.Quantity;
import io.so1s.backend.domain.resource.dto.request.ResourceCreateRequestDto;
import io.so1s.backend.domain.resource.dto.response.ResourceFindResponseDto;
import io.so1s.backend.domain.resource.dto.service.ResourceDto;
import io.so1s.backend.domain.resource.entity.Resource;
import java.util.Map;
import java.util.Optional;
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

  public Quantity toQuantity(String value) {
    return new Quantity(value);
  }

  public ResourceDto toServiceDto(Map<String, Quantity> map) {
    Quantity cpu = map.get("cpu");
    Quantity memory = map.get("memory");
    Quantity gpu = Optional.ofNullable(map.get("nvidia.com/gpu")).orElse(new Quantity("0"));

    return ResourceDto.builder()
        .cpu(cpu)
        .memory(memory)
        .gpu(gpu)
        .cpuLimit(cpu)
        .memoryLimit(memory)
        .gpuLimit(gpu)
        .build();
  }

  public ResourceDto toServiceDto(Resource resource) {
    return ResourceDto.builder()
        .cpu(toQuantity(resource.getCpu()))
        .memory(toQuantity(resource.getMemory()))
        .gpu(toQuantity(resource.getGpu()))
        .cpuLimit(toQuantity(Optional.ofNullable(resource.getCpuLimit()).orElse(resource.getCpu())))
        .memoryLimit(
            toQuantity(Optional.ofNullable(resource.getMemory()).orElse(resource.getMemoryLimit())))
        .gpuLimit(toQuantity(Optional.ofNullable(resource.getGpu()).orElse(resource.getGpuLimit())))
        .build();
  }

}
