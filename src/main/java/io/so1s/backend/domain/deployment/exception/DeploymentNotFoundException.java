package io.so1s.backend.domain.deployment.exception;

import io.so1s.backend.global.error.exception.NotFoundException;

public class DeploymentNotFoundException extends NotFoundException {

  public DeploymentNotFoundException(String message) {
    super(message);
  }
}
