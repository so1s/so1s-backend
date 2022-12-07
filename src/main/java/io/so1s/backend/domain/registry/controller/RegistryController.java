package io.so1s.backend.domain.registry.controller;

import io.so1s.backend.domain.registry.dto.mapper.RegistryMapper;
import io.so1s.backend.domain.registry.dto.request.RegistryUploadRequestDto;
import io.so1s.backend.domain.registry.dto.response.RegistryFindResponseDto;
import io.so1s.backend.domain.registry.service.RegistryService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/registry")
@RequiredArgsConstructor
public class RegistryController {

  private final RegistryService service;

  private final RegistryMapper mapper;

  @PostMapping
  public ResponseEntity<RegistryFindResponseDto> saveRegistry(
      @Valid RegistryUploadRequestDto requestDto) {
    var registry = service.saveRegistry(requestDto);
    var response = mapper.toDto(registry);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }

  @GetMapping
  public ResponseEntity<List<RegistryFindResponseDto>> getRegistries() {
    var response = service.findAll().stream().map(mapper::toDto).collect(Collectors.toList());

    return ResponseEntity
        .ok(response);
  }

}
