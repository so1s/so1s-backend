package io.so1s.backend.domain.test.v1.controller;

import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.deployment.service.DeploymentService;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.test.v1.dto.mapper.ABTestMapper;
import io.so1s.backend.domain.test.v1.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.v1.dto.response.ABTestCreateResponseDto;
import io.so1s.backend.domain.test.v1.dto.response.ABTestDeleteResponseDto;
import io.so1s.backend.domain.test.v1.dto.response.ABTestReadResponseDto;
import io.so1s.backend.domain.test.v1.dto.service.derived.ABTestCreateDto;
import io.so1s.backend.domain.test.v1.dto.service.derived.ABTestUpdateDto;
import io.so1s.backend.domain.test.v1.entity.ABTest;
import io.so1s.backend.domain.test.v1.exception.ABTestNotFoundException;
import io.so1s.backend.domain.test.v1.service.ABTestService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/tests/ab")
@RequiredArgsConstructor
public class ABTestController {

  private final ABTestService abTestService;
  private final DeploymentService deploymentService;
  private final KubernetesService kubernetesService;
  private final ABTestMapper mapper;

  @PostMapping
  public ResponseEntity<ABTestCreateResponseDto> createABTest(
      @Valid @RequestBody ABTestRequestDto requestDto)
      throws DeploymentNotFoundException, DataIntegrityViolationException {

    ABTestCreateDto createDto = abTestService.createABTest(requestDto);
    ABTest abTest = createDto.getEntity();
    boolean success = createDto.getSuccess();

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toCreateDto(success, "AB Test 객체가 생성되었습니다.",
            abTest));
  }

  @PutMapping
  public ResponseEntity<ABTestCreateResponseDto> updateABTest(
      @Valid @RequestBody ABTestRequestDto abTestRequestDto)
      throws ABTestNotFoundException, DeploymentNotFoundException {

    ABTestUpdateDto updateDto = abTestService.updateABTest(abTestRequestDto);
    ABTest abTest = updateDto.getEntity();
    boolean success = updateDto.getSuccess();

    return ResponseEntity.ok(
        mapper.toCreateDto(success, "AB Test 객체가 변경되었습니다.", abTest));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ABTestDeleteResponseDto> deleteABTest(@Valid @PathVariable("id") Long id)
      throws ABTestNotFoundException {
    return ResponseEntity.ok(abTestService.deleteABTest(id));
  }

  @GetMapping
  public ResponseEntity<List<ABTestReadResponseDto>> findDeployments() {
    return ResponseEntity.ok(abTestService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ABTestReadResponseDto> findDeployment(
      @Valid @PathVariable("id") Long id
  ) throws ABTestNotFoundException {
    return ResponseEntity.ok(abTestService.findbyId(id)
        .orElseThrow(() -> new ABTestNotFoundException("주어진 AB Test id와 일치하는 객체를 찾지 못했습니다.")));
  }
}
