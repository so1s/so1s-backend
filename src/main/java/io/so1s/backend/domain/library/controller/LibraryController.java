package io.so1s.backend.domain.library.controller;

import io.so1s.backend.domain.library.dto.response.LibraryFindResponseDto;
import io.so1s.backend.domain.library.service.LibraryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/libraries")
@RequiredArgsConstructor
public class LibraryController {

  private final LibraryService libraryService;

  @GetMapping
  public ResponseEntity<List<LibraryFindResponseDto>> getLibraries() {
    return ResponseEntity.ok(libraryService.findAll());
  }
}
