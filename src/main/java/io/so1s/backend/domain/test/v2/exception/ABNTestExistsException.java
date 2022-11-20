package io.so1s.backend.domain.test.v2.exception;

import io.so1s.backend.global.error.exception.ExistException;

public class ABNTestExistsException extends ExistException {

  public ABNTestExistsException(String message) {
    super(message);
  }
}
