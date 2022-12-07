package io.so1s.backend.domain.registry.exception;

import io.so1s.backend.global.error.exception.NotFoundException;

public class RegistryNotFoundException extends NotFoundException {

  public RegistryNotFoundException(String message) {
    super(message);
  }
}
