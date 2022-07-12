package io.so1s.backend.domain.hello.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {

  @GetMapping
  public ResponseEntity<Map<String, String>> hello() {
    Map<String, String> result = new HashMap<>();

    result.put("comment", "ok");

    return ResponseEntity.ok(result);
  }

}
