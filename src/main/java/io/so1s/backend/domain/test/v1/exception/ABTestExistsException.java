package io.so1s.backend.domain.test.v1.exception;

import io.so1s.backend.global.error.exception.ExistException;

public class ABTestExistsException extends ExistException {

  public ABTestExistsException(String message) {
    super(message);
  }
}
