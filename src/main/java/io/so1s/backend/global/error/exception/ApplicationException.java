package io.so1s.backend.global.error.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

  private ErrorCode errorCode;

  public ApplicationException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }
}
