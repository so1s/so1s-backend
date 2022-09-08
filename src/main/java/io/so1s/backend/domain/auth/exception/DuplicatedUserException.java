package io.so1s.backend.domain.auth.exception;

import io.so1s.backend.global.error.exception.DuplicatedException;

public class DuplicatedUserException extends DuplicatedException {

  public DuplicatedUserException(String message) {
    super(message);
  }
}