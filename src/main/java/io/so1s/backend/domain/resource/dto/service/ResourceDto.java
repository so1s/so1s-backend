package io.so1s.backend.domain.resource.dto.service;

import io.fabric8.kubernetes.api.model.Quantity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {

  Quantity cpu;
  Quantity gpu;
  Quantity memory;
  Quantity cpuLimit;
  Quantity gpuLimit;
  Quantity memoryLimit;

}
