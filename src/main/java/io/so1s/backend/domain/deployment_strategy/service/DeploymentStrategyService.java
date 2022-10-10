package io.so1s.backend.domain.deployment_strategy.service;

import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import io.so1s.backend.domain.deployment_strategy.dto.response.DeploymentStrategyFindResponseDto;
import io.so1s.backend.domain.deployment_strategy.exception.DeploymentStrategyNotFoundException;
import java.util.List;

public interface DeploymentStrategyService {

  DeploymentStrategy findByName(String name) throws DeploymentStrategyNotFoundException;

  List<DeploymentStrategyFindResponseDto> findAll();

}
