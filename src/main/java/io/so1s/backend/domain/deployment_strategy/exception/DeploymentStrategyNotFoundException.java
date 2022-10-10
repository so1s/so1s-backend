package io.so1s.backend.domain.deployment_strategy.exception;

import io.so1s.backend.global.error.exception.NotFoundException;

public class DeploymentStrategyNotFoundException extends NotFoundException {

  public DeploymentStrategyNotFoundException(String message) {
    super(message);
  }
}