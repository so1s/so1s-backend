package io.so1s.backend.domain.model.exception;

import io.so1s.backend.global.error.exception.NotFoundException;

public class ModelMetadataNotFoundException extends NotFoundException {

  public ModelMetadataNotFoundException(String message) {
    super(message);
  }
}
