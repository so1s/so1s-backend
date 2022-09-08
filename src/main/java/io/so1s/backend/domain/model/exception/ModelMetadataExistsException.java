package io.so1s.backend.domain.model.exception;

import io.so1s.backend.global.error.exception.ExistException;

public class ModelMetadataExistsException extends ExistException {

  public ModelMetadataExistsException(String message) {
    super(message);
  }
}
