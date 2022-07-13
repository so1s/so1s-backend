package io.so1s.backend.domain.auth.controller;

import io.so1s.backend.domain.auth.dto.request.SignUpRequestDto;
import io.so1s.backend.domain.auth.dto.response.SignUpResponseDto;
import io.so1s.backend.domain.auth.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/signup")
@RequiredArgsConstructor
public class SignUpController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<SignUpResponseDto> signUp(
      @Valid @RequestBody SignUpRequestDto signUpRequestDto) {

    userService.signUp(signUpRequestDto.getUsername(), signUpRequestDto.getPassword());

    SignUpResponseDto result = SignUpResponseDto.builder().success(true).build();

    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }
}
