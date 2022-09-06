package io.so1s.backend.domain.test.service;

import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.test.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.dto.response.ABTestReadResponseDto;
import io.so1s.backend.domain.test.entity.ABTest;
import io.so1s.backend.domain.test.exception.ABTestNotFoundException;
import java.util.List;
import java.util.Optional;

public interface ABTestService {

  ABTest createABTest(ABTestRequestDto deploymentRequestDto) throws DeploymentNotFoundException;

  ABTest updateABTest(ABTestRequestDto deploymentRequestDto)
      throws ABTestNotFoundException, DeploymentNotFoundException;

  List<ABTestReadResponseDto> findAll();

  Optional<ABTestReadResponseDto> findbyId(Long id);
}
