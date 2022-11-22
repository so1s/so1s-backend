package io.so1s.backend.domain.test.v2.dto.mapper;

import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.test.v2.dto.request.ABNTestRequestDto;
import io.so1s.backend.domain.test.v2.dto.response.ABNTestCreateResponseDto;
import io.so1s.backend.domain.test.v2.dto.response.ABNTestReadResponseDto;
import io.so1s.backend.domain.test.v2.entity.ABNTest;
import org.springframework.dao.DataIntegrityViolationException;

public interface ABNTestMapper {

  ABNTest toABNTest(ABNTestRequestDto dto)
      throws DeploymentNotFoundException, DataIntegrityViolationException;

  ABNTestReadResponseDto toReadDto(ABNTest entity);

  ABNTestCreateResponseDto toCreateResponseDto(Boolean success, String message, ABNTest entity);
}
