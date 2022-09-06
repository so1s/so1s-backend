package io.so1s.backend.global.error.exception;

public class DuplicatedException extends ApplicationException {

  public DuplicatedException(String message) {
    super(message, ErrorCode.ENTITY_DUPLICATED);
  }
}
