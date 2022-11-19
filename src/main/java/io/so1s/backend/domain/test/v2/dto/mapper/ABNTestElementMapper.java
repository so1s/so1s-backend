package io.so1s.backend.domain.test.v2.dto.mapper;

import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.test.v2.dto.common.ABNTestElementDto;
import io.so1s.backend.domain.test.v2.entity.ABNTestElement;

public interface ABNTestElementMapper {

  ABNTestElement toElement(ABNTestElementDto dto)
      throws DeploymentNotFoundException;

  ABNTestElementDto toDto(ABNTestElement entity);
}