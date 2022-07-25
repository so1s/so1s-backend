package io.so1s.backend.domain.deployment.dto.request;

import io.so1s.backend.domain.deployment.entity.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceRequestDto {

  private String cpu;

  private String memory;

  private String gpu;

  private String cpuLimit;

  private String memoryLimit;

  private String gpuLimit;

  public Resource toEntity() {
    return new Resource().builder()
        .cpu(cpu)
        .memory(memory)
        .gpu(gpu)
        .cpuLimit(cpuLimit)
        .memoryLimit(memoryLimit)
        .gpuLimit(gpuLimit)
        .build();
  }
}
