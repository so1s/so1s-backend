package io.so1s.backend.domain.deployment.exception;

import io.so1s.backend.global.error.exception.NotFoundException;

public class LibraryNotFoundException extends NotFoundException {

  public LibraryNotFoundException(String message) {
    super(message);
  }
}
