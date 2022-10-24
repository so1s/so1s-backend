package io.so1s.backend.global.error.exception;

public class NodeResourceExceededException extends ApplicationException {

  public NodeResourceExceededException(String message) {
    super(message, ErrorCode.NODE_RESOURCE_EXCEEDED);
  }


}
