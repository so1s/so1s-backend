package io.so1s.backend.domain.test.v1.exception;

import io.so1s.backend.global.error.exception.NotFoundException;

public class ABTestNotFoundException extends NotFoundException {

  public ABTestNotFoundException(String message) {
    super(message);
  }
}
