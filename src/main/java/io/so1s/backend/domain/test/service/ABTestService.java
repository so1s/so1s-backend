package io.so1s.backend.domain.test.service;

import io.so1s.backend.domain.test.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.dto.response.ABTestDeleteResponseDto;
import io.so1s.backend.domain.test.dto.response.ABTestReadResponseDto;
import io.so1s.backend.domain.test.dto.service.ABTestCreateDto;
import io.so1s.backend.domain.test.entity.ABTest;
import io.so1s.backend.global.error.exception.ABTestNotFoundException;
import io.so1s.backend.global.error.exception.DeploymentNotFoundException;
import java.util.List;
import java.util.Optional;

public interface ABTestService {

  ABTestCreateDto createABTest(ABTestRequestDto abTestRequestDto)
      throws DeploymentNotFoundException;

  ABTest updateABTest(ABTestRequestDto abTestRequestDto)
      throws ABTestNotFoundException, DeploymentNotFoundException;

  ABTestDeleteResponseDto deleteABTest(Long id) throws ABTestNotFoundException;

  List<ABTestReadResponseDto> findAll();

  Optional<ABTestReadResponseDto> findbyId(Long id);
}
