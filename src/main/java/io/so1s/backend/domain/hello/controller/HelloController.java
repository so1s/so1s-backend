package io.so1s.backend.domain.hello.controller;

import io.so1s.backend.domain.hello.dto.response.HelloResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {

  @GetMapping
  public ResponseEntity<HelloResponseDto> hello() {
    HelloResponseDto result = HelloResponseDto.builder().comment("ok").build();

    return ResponseEntity.ok(result);
  }

}
