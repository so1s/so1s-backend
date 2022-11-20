package io.so1s.backend.domain.test.v1.service;

import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.test.v1.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.v1.dto.response.ABTestDeleteResponseDto;
import io.so1s.backend.domain.test.v1.dto.response.ABTestReadResponseDto;
import io.so1s.backend.domain.test.v1.dto.service.derived.ABTestCreateDto;
import io.so1s.backend.domain.test.v1.dto.service.derived.ABTestUpdateDto;
import io.so1s.backend.domain.test.v1.exception.ABTestNotFoundException;
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
