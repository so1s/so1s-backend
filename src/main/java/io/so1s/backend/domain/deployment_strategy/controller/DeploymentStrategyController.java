package io.so1s.backend.domain.deployment_strategy.controller;

import io.so1s.backend.domain.deployment_strategy.dto.response.DeploymentStrategyFindResponseDto;
import io.so1s.backend.domain.deployment_strategy.service.DeploymentStrategyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/deployment-strategies")
@RequiredArgsConstructor
public class DeploymentStrategyController {

  private DeploymentStrategyService deploymentStrategyService;

  @GetMapping
  public ResponseEntity<List<DeploymentStrategyFindResponseDto>> getDeploymentStrategies() {
    return ResponseEntity.ok(deploymentStrategyService.findAll());
  }
}
