package io.so1s.backend.domain.auth.controller;

import io.so1s.backend.domain.auth.dto.response.AuthStatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/status")
@RequiredArgsConstructor
public class AuthStatusController {

  @GetMapping
  public ResponseEntity<AuthStatusResponseDto> checkAuthStatus() {

    // JwtAuthenticationEntryPoint에서 인증 핸들링이 이루어지기에 인증을 완료한 시점의 API Response는 true만 래핑해서 반환함.
    // TODO: 사용자 정보도 반환해야 하는지? /users/me API로 리팩토링해야 할지?
    return ResponseEntity.ok(AuthStatusResponseDto.builder().success(true).build());
  }
}
