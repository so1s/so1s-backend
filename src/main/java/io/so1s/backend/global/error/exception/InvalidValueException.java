package io.so1s.backend.global.error.exception;

public class InvalidValueException extends ApplicationException {

  public InvalidValueException(String value) {
    super(value, ErrorCode.INVALID_INPUT_VALUE);
  }
}
