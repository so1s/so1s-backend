package io.so1s.backend.domain.model.exception;

import io.so1s.backend.global.error.exception.DuplicatedException;

public class DuplicatedModelNameException extends DuplicatedException {

  public DuplicatedModelNameException(String message) {
    super(message);
  }
}
