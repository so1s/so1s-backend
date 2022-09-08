package io.so1s.backend.global.error;

import io.so1s.backend.global.error.exception.ApplicationException;
import io.so1s.backend.global.error.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

  /**
   * @Valid 값 바인딩 못 할시
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), errorCode.getStatus(), e.getMessage());
    final ErrorResponseDto response = ErrorResponseDto.of(errorCode, e.getBindingResult());
    return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
  }

  /**
   * @ModelAttribute 값 바인딩 못 할시
   */
  @ExceptionHandler(BindException.class)
  protected ResponseEntity<ErrorResponseDto> handleBindException(BindException e) {
    ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), errorCode.getStatus(), e.getMessage());
    final ErrorResponseDto response = ErrorResponseDto.of(errorCode, e.getBindingResult());
    return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
  }

  /**
   * 잘못된 HTTP method 호출 시
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  protected ResponseEntity<ErrorResponseDto> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), errorCode.getStatus(), e.getMessage());
    final ErrorResponseDto response = ErrorResponseDto.of(errorCode);
    return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
  }

  /**
   * 권한을 보유하고 있지 않을 때
   */
  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException e) {
    ErrorCode errorCode = ErrorCode.HANDLE_ACCESS_DENIED;
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), errorCode.getStatus(), e.getMessage());
    final ErrorResponseDto response = ErrorResponseDto.of(errorCode);
    return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
  }

  /**
   * 잘못된 인증 값으로 자격 증명시
   */
  @ExceptionHandler(BadCredentialsException.class)
  protected ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
      BadCredentialsException e) {
    ErrorCode errorCode = ErrorCode.HANDLE_ACCESS_DENIED;
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), errorCode.getStatus(), e.getMessage());
    final ErrorResponseDto response = ErrorResponseDto.of(errorCode);
    return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
  }

  /**
   * Application 내 커스텀 예외
   */
  @ExceptionHandler(ApplicationException.class)
  protected ResponseEntity<ErrorResponseDto> handleApplicationException(ApplicationException e) {
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode(), e.getMessage());
    final ErrorResponseDto responseDto = ErrorResponseDto.of(e.getErrorCode(), e.getMessage());
    return new ResponseEntity<>(responseDto, HttpStatus.valueOf(e.getErrorCode().getStatus()));
  }


  /**
   * 나머지
   */
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponseDto> handleException(Exception e) {
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    log.error(LOG_FORMAT, e.getClass().getSimpleName(), errorCode.getStatus(), e.getMessage());
    final ErrorResponseDto response = ErrorResponseDto.of(errorCode);
    return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
  }
}
