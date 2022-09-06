package io.so1s.backend.domain.auth.exception;

import io.so1s.backend.global.error.exception.DuplicatedException;

public class DuplicateUserException extends DuplicatedException {

  public DuplicateUserException(String message) {
    super(message);
  }
}