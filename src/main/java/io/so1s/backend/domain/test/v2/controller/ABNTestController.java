package io.so1s.backend.domain.test.v2.controller;


import io.so1s.backend.domain.test.v1.exception.ABTestNotFoundException;
import io.so1s.backend.domain.test.v2.dto.request.ABNTestRequestDto;
import io.so1s.backend.domain.test.v2.dto.response.ABNTestDeleteResponseDto;
import io.so1s.backend.domain.test.v2.dto.response.ABNTestReadResponseDto;
import io.so1s.backend.domain.test.v2.dto.service.derived.ABNTestCreateDto;
import io.so1s.backend.domain.test.v2.dto.service.derived.ABNTestUpdateDto;
import io.so1s.backend.domain.test.v2.exception.ABNTestNotFoundException;
import io.so1s.backend.domain.test.v2.service.ABNTestService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/tests/ab")
@RequiredArgsConstructor
public class ABNTestController {

  private final ABNTestService service;

  @GetMapping
  public ResponseEntity<List<ABNTestReadResponseDto>> findDeployments() {

    return ResponseEntity.ok(service.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ABNTestReadResponseDto> findDeployment(@Valid @PathVariable("id") Long id)
      throws ABNTestNotFoundException {

    return ResponseEntity.ok(service.findById(id)
        .orElseThrow(() -> new ABTestNotFoundException("주어진 AB Test id와 일치하는 객체를 찾지 못했습니다.")));
  }

  @PostMapping
  public ResponseEntity<ABNTestCreateDto> createABNTest(
      @Valid @RequestBody ABNTestRequestDto requestDto) {
    ABNTestCreateDto createDto = service.createABNTest(requestDto);

    return ResponseEntity.ok(ABNTestCreateDto.builder()
        .entity(createDto.getEntity())
        .success(createDto.getSuccess())
        .build());
  }

  @PutMapping
  public ResponseEntity<ABNTestCreateDto> updateABNTest(
      @Valid @RequestBody ABNTestRequestDto requestDto) {
    ABNTestUpdateDto updateDto = service.updateABNTest(requestDto);

    return ResponseEntity.ok(ABNTestCreateDto.builder()
        .entity(updateDto.getEntity())
        .success(updateDto.getSuccess())
        .build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ABNTestDeleteResponseDto> deleteABNTest(@Valid @PathVariable("id") Long id)
      throws ABNTestNotFoundException {

    ABNTestDeleteResponseDto deleteDto = service.deleteABNTest(id);

    return ResponseEntity.ok(ABNTestDeleteResponseDto.builder()
        .success(deleteDto.getSuccess())
        .message(deleteDto.getMessage())
        .build());
  }

}