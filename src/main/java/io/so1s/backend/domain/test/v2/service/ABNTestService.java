package io.so1s.backend.domain.test.v2.service;

import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.test.v2.dto.request.ABNTestRequestDto;
import io.so1s.backend.domain.test.v2.dto.response.ABNTestDeleteResponseDto;
import io.so1s.backend.domain.test.v2.dto.response.ABNTestReadResponseDto;
import io.so1s.backend.domain.test.v2.dto.service.derived.ABNTestCreateDto;
import io.so1s.backend.domain.test.v2.dto.service.derived.ABNTestUpdateDto;
import io.so1s.backend.domain.test.v2.exception.ABNTestNotFoundException;
import java.util.List;
import java.util.Optional;

public interface ABNTestService {

  ABNTestCreateDto createABNTest(ABNTestRequestDto requestDto)
      throws DeploymentNotFoundException;

  ABNTestUpdateDto updateABNTest(ABNTestRequestDto requestDto)
      throws ABNTestNotFoundException, DeploymentNotFoundException;

  ABNTestDeleteResponseDto deleteABNTest(Long id) throws ABNTestNotFoundException;

  List<ABNTestReadResponseDto> findAll();

  Optional<ABNTestReadResponseDto> findById(Long id);
}
