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
  PAGE_NOT_FOUND(404, "Common002", "Invalid URL"),
  METHOD_NOT_ALLOWED(405, "Common003", "Invalid Method"),
  INVALID_TYPE_VALUE(400, "Common004", "Invalid Type Value"),
  HANDLE_ACCESS_DENIED(403, "Common005", "Access is Denied"),
  INTERNAL_SERVER_ERROR(500, "Common006", "Server Error"),
  ENTITY_NOT_FOUND(404, "Common007", "Entity Not Found"),
  ENTITY_DUPLICATED(409, "Common008", "Entity Duplicated"),
  ENTITY_EXIST(409, "Common009", "Entity Exist"),

  //Auth
  UNABLE_TO_CREATE_USER(403, "Auth001", "Unable to create User"),

  //AWS

  //Deployment
  UPDATE_FAILED(421, "Deployment001", "Unable to update Deployment"),

  //Kubernetes
  TOO_MANY_BUILD_REQUEST(429, "Kubernetes001", "Too Many Build Request"),

  //Model

  //Test

  ;

  private final int status;
  private final String code;
  private final String message;
}
