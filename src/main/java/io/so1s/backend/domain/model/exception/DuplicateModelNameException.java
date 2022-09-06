package io.so1s.backend.domain.model.exception;

import io.so1s.backend.global.error.exception.DuplicatedException;

public class DuplicateModelNameException extends DuplicatedException {

  public DuplicateModelNameException(String message) {
    super(message);
  }
}
