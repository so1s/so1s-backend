package io.so1s.backend.global.error.exception;

public class ExistException extends ApplicationException {

  public ExistException(String message) {
    super(message, ErrorCode.ENTITY_EXIST);
  }
}
