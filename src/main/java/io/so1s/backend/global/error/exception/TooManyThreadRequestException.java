package io.so1s.backend.global.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE)
public class TooManyThreadRequestException extends RuntimeException {

  public TooManyThreadRequestException(String message) {
    super(message);
  }
}
