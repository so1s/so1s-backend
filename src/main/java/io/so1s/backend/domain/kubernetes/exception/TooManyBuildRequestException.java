package io.so1s.backend.domain.kubernetes.exception;

import io.so1s.backend.global.error.exception.ApplicationException;
import io.so1s.backend.global.error.exception.ErrorCode;

public class TooManyBuildRequestException extends ApplicationException {

  public TooManyBuildRequestException(String message) {
    super(message, ErrorCode.TOO_MANY_BUILD_REQUEST);
  }
}
