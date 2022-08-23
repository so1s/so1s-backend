package io.so1s.backend.domain.test.dto.mapper;

import io.so1s.backend.domain.test.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.dto.response.ABTestCreateResponseDto;
import io.so1s.backend.domain.test.dto.response.ABTestReadResponseDto;
import io.so1s.backend.domain.test.entity.ABTest;
import io.so1s.backend.global.error.exception.DeploymentNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;

public interface ABTestMapper {

  ABTest toABTest(ABTestRequestDto dto)
      throws DeploymentNotFoundException, DataIntegrityViolationException;

  ABTestReadResponseDto toReadDto(ABTest entity);

  ABTestCreateResponseDto toCreateDto(Boolean success, String message, ABTest entity);
}
