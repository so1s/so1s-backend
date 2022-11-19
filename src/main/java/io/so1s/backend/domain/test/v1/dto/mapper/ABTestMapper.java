package io.so1s.backend.domain.test.v1.dto.mapper;

import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.test.v1.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.v1.dto.response.ABTestCreateResponseDto;
import io.so1s.backend.domain.test.v1.dto.response.ABTestReadResponseDto;
import io.so1s.backend.domain.test.v1.entity.ABTest;
import org.springframework.dao.DataIntegrityViolationException;

public interface ABTestMapper {

  ABTest toABTest(ABTestRequestDto dto)
      throws DeploymentNotFoundException, DataIntegrityViolationException;

  ABTestReadResponseDto toReadDto(ABTest entity);

  ABTestCreateResponseDto toCreateDto(Boolean success, String message, ABTest entity);
}
