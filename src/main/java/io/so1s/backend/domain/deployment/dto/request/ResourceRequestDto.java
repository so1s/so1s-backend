package io.so1s.backend.domain.deployment.dto.request;

import io.so1s.backend.domain.deployment.entity.Resource;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceRequestDto {

  @NotBlank
  private String cpu;

  @NotBlank
  private String memory;

  @NotBlank
  private String gpu;

  @NotBlank
  private String cpuLimit;

  @NotBlank
  private String memoryLimit;

  @NotBlank
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
