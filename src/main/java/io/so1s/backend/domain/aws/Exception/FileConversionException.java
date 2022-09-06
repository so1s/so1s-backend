package io.so1s.backend.domain.aws.Exception;


import io.so1s.backend.global.error.exception.InvalidValueException;

public class FileConversionException extends InvalidValueException {

  public FileConversionException(String message) {
    super(message);
  }
}
