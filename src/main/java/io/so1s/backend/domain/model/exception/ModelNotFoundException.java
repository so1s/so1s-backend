package io.so1s.backend.domain.model.exception;

import io.so1s.backend.global.error.exception.NotFoundException;

public class ModelNotFoundException extends NotFoundException {

  public ModelNotFoundException(String message) {
    super(message);
  }
}
