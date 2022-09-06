package io.so1s.backend.global.error.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = Shape.OBJECT)
public enum ErrorCode {

  //Common
  INVALID_INPUT_VALUE(400, "Common001", "Invalid Input Value"),
  METHOD_NOT_ALLOWED(405, "Common002", "Invalid Input Value"),
  ENTITY_NOT_FOUND(404, "Common003", "Entity Not Found"),
  INTERNAL_SERVER_ERROR(500, "Common004", "Server Error"),
  INVALID_TYPE_VALUE(400, "Common005", "Invalid Type Value"),
  HANDLE_ACCESS_DENIED(403, "Common006", "Access is Denied"),
  ENTITY_DUPLICATED(409, "Common007", "Entity Duplicated"),

  //Auth
  UNABLE_TO_CREATE_USER(403, "Auth001", "Unable to Create User"),

  //AWS

  //Deployment

  //Kubernetes
  TOO_MANY_BUILD_REQUEST(429, "Common008", "Too Many Build Request"),

  //Model

  //Test

  ;

  private final int status;
  private final String code;
  private final String message;
}
