package io.so1s.backend.domain.resource.exception;

import io.so1s.backend.global.error.exception.NotFoundException;

public class ResourceNotFoundException extends NotFoundException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
