package io.so1s.backend.domain.deployment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentFindResponseDto {

  private String age;
  private String deploymentName;
  private String status;
  private String endPoint;
  private String strategy;

  private String modelName;
  private String modelVersion;

  private String cpu;
  private String memory;
  private String gpu;
  private String cpuLimit;
  private String memoryLimit;
  private String gpuLimit;
}
