package io.so1s.backend.domain.test.v1.service;

import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.deployment.service.DeploymentService;
import io.so1s.backend.domain.test.v1.dto.mapper.ABTestMapper;
import io.so1s.backend.domain.test.v1.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.v1.dto.response.ABTestDeleteResponseDto;
import io.so1s.backend.domain.test.v1.dto.response.ABTestReadResponseDto;
import io.so1s.backend.domain.test.v1.dto.service.derived.ABTestCreateDto;
import io.so1s.backend.domain.test.v1.dto.service.derived.ABTestUpdateDto;
import io.so1s.backend.domain.test.v1.entity.ABTest;
import io.so1s.backend.domain.test.v1.exception.ABTestNotFoundException;
import io.so1s.backend.domain.test.v1.repository.ABTestRepository;
import io.so1s.backend.domain.test.v1.service.internal.ABTestKubernetesService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ABTestServiceImpl implements ABTestService {

  private final DeploymentService deploymentService;
  private final ABTestKubernetesService kubernetesService;
  private final ABTestRepository repository;
  private final ABTestMapper mapper;

  @Transactional
  @Override
  public ABTestCreateDto createABTest(ABTestRequestDto dto) throws DeploymentNotFoundException {
    // TODO: Manage wildcard subdomain ingress programmatically using route 53 & external-dns & fabric8 kubernetes client
    ABTest entity = repository.save(mapper.toABTest(dto));
    boolean success = kubernetesService.deployABTest(entity);

    return ABTestCreateDto.builder()
        .entity(entity)
        .success(success)
        .build();
  }

  @Transactional
  @Override
  public ABTestUpdateDto updateABTest(ABTestRequestDto dto)
      throws ABTestNotFoundException, DeploymentNotFoundException {
    ABTest entity = repository.findByName(dto.getName())
        .orElseThrow(() -> new ABTestNotFoundException("주어진 Name과 일치하는 AB Test 객체를 찾지 못했습니다."));

    Deployment a = deploymentService.findById(dto.getA()).orElseThrow(
        () -> new DeploymentNotFoundException("주어진 Deployment A id와 일치하는 객체를 찾지 못했습니다."));
    Deployment b = deploymentService.findById(dto.getB()).orElseThrow(
        () -> new DeploymentNotFoundException("주어진 Deployment B id와 일치하는 객체를 찾지 못했습니다."));

    entity.update(a, b, dto.getDomain());
    boolean success = kubernetesService.deployABTest(entity);

    return ABTestUpdateDto.builder()
        .entity(entity)
        .success(success)
        .build();
  }

  @Override
  public ABTestDeleteResponseDto deleteABTest(Long id) throws ABTestNotFoundException {
    ABTest entity = repository.findById(id)
        .orElseThrow(() -> new ABTestNotFoundException("주어진 ID와 일치하는 AB Test 객체를 찾지 못했습니다."));

    boolean result = kubernetesService.deleteABTest(entity);

    if (!result) {
      return ABTestDeleteResponseDto.builder()
          .success(false)
          .message("AB Test 삭제에 실패했습니다.")
          .build();
    }

    repository.delete(entity);

    return ABTestDeleteResponseDto.builder()
        .success(true)
        .message("AB Test 삭제에 성공했습니다.")
        .build();
  }

  @Override
  public List<ABTestReadResponseDto> findAll() {
    return repository.findAll().stream().map(mapper::toReadDto).collect(Collectors.toList());
  }

  @Override
  public Optional<ABTestReadResponseDto> findbyId(Long id) {
    return repository.findById(id).map(mapper::toReadDto);
  }
}
