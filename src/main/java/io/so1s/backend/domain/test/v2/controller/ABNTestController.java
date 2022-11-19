package io.so1s.backend.domain.test.v2.controller;


import io.so1s.backend.domain.test.v2.dto.request.ABNTestRequestDto;
import io.so1s.backend.domain.test.v2.dto.response.ABNTestDeleteResponseDto;
import io.so1s.backend.domain.test.v2.dto.service.derived.ABNTestCreateDto;
import io.so1s.backend.domain.test.v2.dto.service.derived.ABNTestUpdateDto;
import io.so1s.backend.domain.test.v2.entity.ABNTest;
import io.so1s.backend.domain.test.v2.exception.ABNTestNotFoundException;
import io.so1s.backend.domain.test.v2.service.ABNTestService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

  private final ABNTestService abnTestService;

  @PostMapping
  public ResponseEntity<ABNTestCreateDto> createABNTest(
      @Valid @RequestBody ABNTestRequestDto requestDto) {
    ABNTestUpdateDto updateDto = abnTestService.createABNTest(requestDto);
    ABNTest abnTest = updateDto.getEntity();
    boolean success = updateDto.getSuccess();

    return null;
  }

  @PutMapping
  public ResponseEntity<ABNTestCreateDto> updateABNTest(
      @Valid @RequestBody ABNTestRequestDto requestDto) {
    return null;
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ABNTestDeleteResponseDto> deleteABNTest(@Valid @PathVariable("id") Long id)
      throws ABNTestNotFoundException {
    return null;
  }

}
