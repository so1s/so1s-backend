package io.so1s.backend.domain.model.exception;

import io.so1s.backend.global.error.exception.DuplicatedException;

public class DataTypeNotFoundException extends DuplicatedException {

  public DataTypeNotFoundException(String message) {
    super(message);
  }
}
