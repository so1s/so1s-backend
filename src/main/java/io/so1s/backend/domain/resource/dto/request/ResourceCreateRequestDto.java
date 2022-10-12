package io.so1s.backend.domain.resource.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceCreateRequestDto {

  @NotBlank
  private String name;

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
}
