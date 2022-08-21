package io.so1s.backend.domain.test.dto.mapper;

import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.service.DeploymentService;
import io.so1s.backend.domain.test.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.dto.response.ABTestCreateResponseDto;
import io.so1s.backend.domain.test.dto.response.ABTestReadResponseDto;
import io.so1s.backend.domain.test.entity.ABTest;
import io.so1s.backend.global.error.exception.DeploymentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ABTestMapperImpl implements ABTestMapper {

  private final DeploymentService deploymentService;


  @Override
  public ABTest toABTest(ABTestRequestDto dto)
      throws DeploymentNotFoundException, DataIntegrityViolationException {
    Deployment a = deploymentService.findById(dto.getAId()).orElseThrow(
        () -> new DeploymentNotFoundException("주어진 Deployment A id와 일치하는 객체를 찾지 못했습니다."));
    Deployment b = deploymentService.findById(dto.getBId()).orElseThrow(
        () -> new DeploymentNotFoundException("주어진 Deployment B id와 일치하는 객체를 찾지 못했습니다."));

    return ABTest.builder()
        .name(dto.getName())
        .a(a)
        .b(b)
        .domain(dto.getDomain())
        .build();
  }

  @Override
  public ABTestReadResponseDto toReadDto(ABTest entity) {
    return ABTestReadResponseDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .aId(entity.getA().getId())
        .bId(entity.getB().getId())
        .domain(entity.getDomain())
        .build();
  }

  @Override
  public ABTestCreateResponseDto toCreateDto(String message, ABTest entity) {
    return ABTestCreateResponseDto.builder()
        .message(message)
        .data(toReadDto(entity))
        .build();
  }
}
