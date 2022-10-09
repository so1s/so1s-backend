package io.so1s.backend.domain.deployment_strategy.dto.mapper;

import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import io.so1s.backend.domain.deployment_strategy.dto.response.DeploymentStrategyFindResponseDto;
import org.springframework.stereotype.Component;

@Component
public class DeploymentStrategyMapper {

  public DeploymentStrategyFindResponseDto toDto(DeploymentStrategy entity) {
    return DeploymentStrategyFindResponseDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .build();
  }
}
