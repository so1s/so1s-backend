package io.so1s.backend.domain.test.v2.dto.mapper;

import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.deployment.service.DeploymentService;
import io.so1s.backend.domain.test.v2.dto.request.ABNTestRequestDto;
import io.so1s.backend.domain.test.v2.dto.response.ABNTestCreateResponseDto;
import io.so1s.backend.domain.test.v2.dto.response.ABNTestReadResponseDto;
import io.so1s.backend.domain.test.v2.entity.ABNTest;
import io.so1s.backend.domain.test.v2.repository.ABNTestElementRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ABNTestMapperImpl implements ABNTestMapper {

  private final DeploymentService deploymentService;
  private final ABNTestElementRepository elementRepository;
  private final ABNTestElementMapper elementMapper;

  @Autowired
  @Lazy
  private ABNTestMapper self;

  @Transactional
  @Override
  public ABNTest toABNTest(ABNTestRequestDto dto)
      throws DeploymentNotFoundException, DataIntegrityViolationException {
    var elements = dto.getElements().stream()
        .map(elementMapper::toElement)
        .collect(Collectors.toList());

    return ABNTest.builder()
        .name(dto.getName())
        .elements(elements)
        .domain(dto.getDomain())
        .build();
  }


  @Transactional
  @Override
  public ABNTestReadResponseDto toReadDto(ABNTest entity) {
    var elements = entity.getElements().stream()
        .map(elementMapper::toDto)
        .collect(Collectors.toList());

    return ABNTestReadResponseDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .elements(elements)
        .domain(entity.getDomain())
        .build();
  }

  @Override
  public ABNTestCreateResponseDto toCreateDto(Boolean success, String message, ABNTest entity) {
    return ABNTestCreateResponseDto.builder()
        .success(success)
        .message(message)
        .data(self.toReadDto(entity))
        .build();
  }
}
