package io.so1s.backend.domain.test.service;

import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.test.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.dto.response.ABTestDeleteResponseDto;
import io.so1s.backend.domain.test.dto.response.ABTestReadResponseDto;
import io.so1s.backend.domain.test.entity.ABTest;
import io.so1s.backend.domain.test.exception.ABTestNotFoundException;
import io.so1s.backend.domain.test.dto.service.ABTestCreateDto;
import io.so1s.backend.domain.test.dto.service.ABTestUpdateDto;
import java.util.List;
import java.util.Optional;

public interface ABTestService {

  ABTestCreateDto createABTest(ABTestRequestDto abTestRequestDto)
      throws DeploymentNotFoundException;

  ABTestUpdateDto updateABTest(ABTestRequestDto abTestRequestDto)
      throws ABTestNotFoundException, DeploymentNotFoundException;

  ABTestDeleteResponseDto deleteABTest(Long id) throws ABTestNotFoundException;

  List<ABTestReadResponseDto> findAll();

  Optional<ABTestReadResponseDto> findbyId(Long id);
}
