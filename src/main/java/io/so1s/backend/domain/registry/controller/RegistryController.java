package io.so1s.backend.domain.registry.controller;

import io.so1s.backend.domain.registry.dto.mapper.RegistryMapper;
import io.so1s.backend.domain.registry.dto.request.RegistryUploadRequestDto;
import io.so1s.backend.domain.registry.dto.response.RegistryDeleteResponseDto;
import io.so1s.backend.domain.registry.dto.response.RegistryFindResponseDto;
import io.so1s.backend.domain.registry.service.RegistryService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/registries")
@RequiredArgsConstructor
public class RegistryController {

  private final RegistryService service;

  private final RegistryMapper mapper;

  @PostMapping
  public ResponseEntity<RegistryFindResponseDto> saveRegistry(
      @RequestBody @Valid RegistryUploadRequestDto requestDto) {
    var registry = service.saveRegistry(requestDto);
    var response = mapper.toDto(registry);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }

  @DeleteMapping("/{registry_id}")
  public ResponseEntity<RegistryDeleteResponseDto> deleteRegistry(
      @Valid @PathVariable("registry_id") Long id) {

    boolean success = service.deleteRegistryById(id);

    var response = RegistryDeleteResponseDto.builder()
        .success(success)
        .message(String.format("레지스트리 삭제에 %s했습니다.", success ? "성공" : "실패"))
        .build();

    return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
        .body(response);
  }

  @GetMapping
  public ResponseEntity<List<RegistryFindResponseDto>> getRegistries() {
    var response = service.findAll().stream().map(mapper::toDto).collect(Collectors.toList());

    return ResponseEntity
        .ok(response);
  }

}
