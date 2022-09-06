package io.so1s.backend.global.error;

import io.so1s.backend.global.error.exception.ApplicationException;
import io.so1s.backend.global.error.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final String LOG_FORMAT = "@@DEV, Class : {}, Code : {}, Message : {}";

  /**
   * @Valid 값 바인딩 못 할시 예외 처리
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), HttpStatus.BAD_REQUEST, e.getMessage());
    final ErrorResponseDto response = ErrorResponseDto.of(ErrorCode.INVALID_INPUT_VALUE,
        e.getBindingResult());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * @ModelAttribute 값 바인딩 못 할시 예외 처리
   */
  @ExceptionHandler(BindException.class)
  protected ResponseEntity<ErrorResponseDto> handleBindException(BindException e) {
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), HttpStatus.BAD_REQUEST, e.getMessage());
    final ErrorResponseDto response = ErrorResponseDto.of(ErrorCode.INVALID_INPUT_VALUE,
        e.getBindingResult());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * 잘못된 HTTP method 호출 시 예외 처리
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  protected ResponseEntity<ErrorResponseDto> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), HttpStatus.METHOD_NOT_ALLOWED,
        e.getMessage());
    final ErrorResponseDto response = ErrorResponseDto.of(ErrorCode.METHOD_NOT_ALLOWED);
    return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
  }

  /**
   * 권한을 보유하고 있지 않을 때 예외 처리
   */
  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException e) {
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), ErrorCode.HANDLE_ACCESS_DENIED.getStatus(),
        e.getMessage());
    final ErrorResponseDto response = ErrorResponseDto.of(ErrorCode.HANDLE_ACCESS_DENIED);
    return new ResponseEntity<>(response,
        HttpStatus.valueOf(ErrorCode.HANDLE_ACCESS_DENIED.getStatus()));
  }

  /**
   * Application 내 커스텀 예외 처리
   */
  @ExceptionHandler(ApplicationException.class)
  protected ResponseEntity<ErrorResponseDto> handleApplicationException(ApplicationException e) {
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode(), e.getMessage());

    ErrorResponseDto responseDto = null;
    if (e.getFieldError() != null) {
      responseDto = ErrorResponseDto.of(e.getErrorCode(), e.getMessage(), e.getFieldError());
    } else {
      responseDto = ErrorResponseDto.of(e.getErrorCode(), e.getMessage());
    }
    return new ResponseEntity<>(responseDto, HttpStatus.valueOf(e.getErrorCode().getStatus()));
  }


  /**
   * 나머지
   */
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponseDto> handleException(Exception e) {
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR,
        e.getMessage());
    final ErrorResponseDto responseDto = ErrorResponseDto.of(ErrorCode.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
