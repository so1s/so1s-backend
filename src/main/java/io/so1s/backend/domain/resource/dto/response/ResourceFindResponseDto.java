package io.so1s.backend.domain.resource.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceFindResponseDto {

  private Long id;

  private String cpu;

  private String memory;

  private String gpu;

  private String cpuLimit;

  private String memoryLimit;

  private String gpuLimit;
}
