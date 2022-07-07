package io.so1s.backend.global.domain.healthcheck.controller;

import io.so1s.backend.global.domain.healthcheck.dto.response.HealthCheckResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/livez")
public class HealthCheckController {

  @GetMapping
  public ResponseEntity<?> checkLiveness() {
    return ResponseEntity.ok(HealthCheckResponseDto.builder().success(true).build());
  }

}
