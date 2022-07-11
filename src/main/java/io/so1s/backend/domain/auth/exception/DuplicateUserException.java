package io.so1s.backend.domain.auth.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DuplicateUserException extends RuntimeException {

  public DuplicateUserException(String message) {
    super(message);
  }
}