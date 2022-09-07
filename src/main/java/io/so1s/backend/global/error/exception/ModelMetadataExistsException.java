package io.so1s.backend.global.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ModelMetadataExistsException extends RuntimeException {

  public ModelMetadataExistsException(String message) {
    super(message);
  }
}
