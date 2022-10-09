package io.so1s.backend.domain.deployment_strategy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentStrategyFindResponseDto {

  private Long id;
  private String name;
}
