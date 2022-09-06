package io.so1s.backend.domain.aws.Exception;

import io.so1s.backend.global.error.exception.InvalidValueException;

public class UnsupportedFileFormatException extends InvalidValueException {

  public UnsupportedFileFormatException(String message) {
    super(message);
  }
}
