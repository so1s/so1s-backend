package io.so1s.backend.domain.kubernetes.exception;

import io.so1s.backend.global.error.exception.NotFoundException;

public class NodeNotFoundException extends NotFoundException {

  public NodeNotFoundException(String message) {
    super(message);
  }

}
