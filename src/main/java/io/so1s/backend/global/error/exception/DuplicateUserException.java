package io.so1s.backend.global.error.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DuplicateUserException extends RuntimeException {

  public DuplicateUserException(String message) {
    super(message);
  }
}