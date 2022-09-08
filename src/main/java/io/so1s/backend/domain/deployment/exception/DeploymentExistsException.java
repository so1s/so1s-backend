package io.so1s.backend.domain.deployment.exception;

import io.so1s.backend.global.error.exception.ExistException;

public class DeploymentExistsException extends ExistException {

  public DeploymentExistsException(String message) {
    super(message);
  }
}
