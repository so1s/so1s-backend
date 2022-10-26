package io.so1s.backend.domain.deployment.exception;

import io.so1s.backend.global.error.exception.ApplicationException;
import io.so1s.backend.global.error.exception.ErrorCode;

public class DeploymentUpdateFailedException extends ApplicationException {

  public DeploymentUpdateFailedException(String message) {
    super(message, ErrorCode.UPDATE_FAILED);
  }
}
