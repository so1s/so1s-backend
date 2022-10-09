package io.so1s.backend.domain.deployment_strategy.service;

import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import io.so1s.backend.domain.deployment_strategy.dto.mapper.DeploymentStrategyMapper;
import io.so1s.backend.domain.deployment_strategy.dto.response.DeploymentStrategyFindResponseDto;
import io.so1s.backend.domain.deployment_strategy.exception.DeploymentStrategyNotFoundException;
import io.so1s.backend.domain.deployment_strategy.repository.DeploymentStrategyRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeploymentStrategyServiceImpl implements DeploymentStrategyService {

  private final DeploymentStrategyRepository deploymentStrategyRepository;
  private final DeploymentStrategyMapper deploymentStrategyMapper;

  @Override
  public DeploymentStrategy findByName(String name) throws DeploymentStrategyNotFoundException {
    return deploymentStrategyRepository.findByName(name)
        .orElseThrow(() -> new DeploymentStrategyNotFoundException("Invalid Deployment Strategy"));
  }

  @Override
  public List<DeploymentStrategyFindResponseDto> findAll() {
    return deploymentStrategyRepository.findAll().stream().map(deploymentStrategyMapper::toDto)
        .collect(Collectors.toList());
  }

}
