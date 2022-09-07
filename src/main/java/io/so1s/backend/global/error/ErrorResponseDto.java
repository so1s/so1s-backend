package io.so1s.backend.global.error;

import io.so1s.backend.global.error.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

  private String message;
  private String code;
  private int status;
  private List<FieldError> errors;

  private ErrorResponseDto(ErrorCode e) {
    this.status = e.getStatus();
    this.code = e.getCode();
    this.message = e.getMessage();
    this.errors = new ArrayList<>();
  }

  private ErrorResponseDto(ErrorCode e, String message) {
    this.status = e.getStatus();
    this.code = e.getCode();
    this.message = message;
    this.errors = new ArrayList<>();
  }

  private ErrorResponseDto(ErrorCode e, List<FieldError> errors) {
    this.status = e.getStatus();
    this.code = e.getCode();
    this.message = e.getMessage();
    this.errors = errors;
  }

  private ErrorResponseDto(ErrorCode e, String message, List<FieldError> errors) {
    this.status = e.getStatus();
    this.code = e.getCode();
    this.message = message;
    this.errors = errors;
  }

  public static ErrorResponseDto of(ErrorCode code) {
    return new ErrorResponseDto(code);
  }

  public static ErrorResponseDto of(ErrorCode code, String message) {
    return new ErrorResponseDto(code, message);
  }

  public static ErrorResponseDto of(ErrorCode code, BindingResult bindingResult) {
    return new ErrorResponseDto(code, FieldError.of(bindingResult));
  }

  public static ErrorResponseDto of(ErrorCode code, String message, BindingResult bindingResult) {
    return new ErrorResponseDto(code, message, FieldError.of(bindingResult));
  }

  public static ErrorResponseDto of(MethodArgumentTypeMismatchException e) {
    final String value = e.getValue() == null ? "" : e.getValue().toString();
    final List<ErrorResponseDto.FieldError> errors = ErrorResponseDto.FieldError.of(
        e.getName(), value, e.getErrorCode());
    return new ErrorResponseDto(ErrorCode.INVALID_TYPE_VALUE, errors);
  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  public static class FieldError {

    private String field;
    private String value;
    private String reason;

    private FieldError(final String field, final String value, final String reason) {
      this.field = field;
      this.value = value;
      this.reason = reason;
    }

    public static List<FieldError> of(final String field, final String value, final String reason) {
      List<FieldError> fieldErrors = new ArrayList<>();
      fieldErrors.add(new FieldError(field, value, reason));
      return fieldErrors;
    }

    private static List<FieldError> of(final BindingResult bindingResult) {
      final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
      return fieldErrors.stream()
          .map(error -> new FieldError(
              error.getField(),
              error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
              error.getDefaultMessage()))
          .collect(Collectors.toList());
    }
  }
}
