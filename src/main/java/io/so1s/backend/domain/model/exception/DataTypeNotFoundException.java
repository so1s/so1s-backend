package io.so1s.backend.domain.model.exception;

import io.so1s.backend.global.error.exception.NotFoundException;

public class DataTypeNotFoundException extends NotFoundException {

  public DataTypeNotFoundException(String message) {
    super(message);
  }
}
