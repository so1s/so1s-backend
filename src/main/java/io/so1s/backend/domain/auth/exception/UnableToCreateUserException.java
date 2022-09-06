package io.so1s.backend.domain.auth.exception;

import io.so1s.backend.global.error.exception.ApplicationException;
import io.so1s.backend.global.error.exception.ErrorCode;

public class UnableToCreateUserException extends ApplicationException {

  public UnableToCreateUserException(String message) {
    super(message, ErrorCode.UNABLE_TO_CREATE_USER);
  }
}