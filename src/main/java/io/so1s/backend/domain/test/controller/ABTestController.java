package io.so1s.backend.domain.test.controller;

import io.so1s.backend.domain.deployment.service.DeploymentService;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.test.dto.mapper.ABTestMapper;
import io.so1s.backend.domain.test.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.dto.response.ABTestCreateResponseDto;
import io.so1s.backend.domain.test.dto.response.ABTestReadResponseDto;
import io.so1s.backend.domain.test.entity.ABTest;
import io.so1s.backend.domain.test.service.ABTestService;
import io.so1s.backend.global.error.exception.ABTestNotFoundException;
import io.so1s.backend.global.error.exception.DeploymentNotFoundException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    ABTest abTest = abTestService.createABTest(requestDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toCreateDto(kubernetesService.deployABTest(abTest), "AB Test 객체가 생성되었습니다.",
            abTest));
  }

  @PutMapping
  public ResponseEntity<ABTestCreateResponseDto> updateABTest(
      @Valid @RequestBody ABTestRequestDto abTestRequestDto)
      throws ABTestNotFoundException, DeploymentNotFoundException {

    ABTest abTest = abTestService.updateABTest(abTestRequestDto);

    return ResponseEntity.ok(
        mapper.toCreateDto(kubernetesService.deployABTest(abTest), "AB Test 객체가 변경되었습니다.", abTest));
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
