package io.so1s.backend.global.error.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UnableToCreateUserException extends RuntimeException {

  public UnableToCreateUserException(String message) {
    super(message);
  }
}