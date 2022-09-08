package io.so1s.backend.global.error.exception;

public class NotFoundException extends ApplicationException {

  public NotFoundException(String message) {
    super(message, ErrorCode.ENTITY_NOT_FOUND);
  }
}
