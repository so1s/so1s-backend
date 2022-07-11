package io.so1s.backend.global.error.handler;


import io.so1s.backend.global.error.dto.response.ErrorResponseDto;
import io.so1s.backend.global.error.exception.DuplicateUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(DuplicateUserException.class)
  protected ResponseEntity<Object> handleDuplicateUserException(DuplicateUserException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    String message = ex.getMessage();

    log.error("handleDuplicateUserException {}", message);

    ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
        .message(message)
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(errorResponseDto);
  }
}
