package io.so1s.backend.global.error.exception;

import io.so1s.backend.global.error.ErrorResponseDto.FieldError;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

  private ErrorCode errorCode;
  private FieldError fieldError;

  public ApplicationException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
    fieldError = null;
  }

  public ApplicationException(String message, ErrorCode errorCode, FieldError fieldError) {
    super(message);
    this.errorCode = errorCode;
    this.fieldError = fieldError;
  }
}
