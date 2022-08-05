package io.so1s.backend.global.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ModelMetadataNotFoundException extends RuntimeException {

  public ModelMetadataNotFoundException(String message) {
    super(message);
  }
}
