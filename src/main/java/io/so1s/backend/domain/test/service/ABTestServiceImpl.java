package io.so1s.backend.domain.test.service;

import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.service.DeploymentService;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.test.dto.mapper.ABTestMapper;
import io.so1s.backend.domain.test.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.dto.response.ABTestReadResponseDto;
import io.so1s.backend.domain.test.entity.ABTest;
import io.so1s.backend.domain.test.repository.ABTestRepository;
import io.so1s.backend.global.error.exception.ABTestNotFoundException;
import io.so1s.backend.global.error.exception.DeploymentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ABTestServiceImpl implements ABTestService {

  private DeploymentService deploymentService;
  private KubernetesService kubernetesService;
  private ABTestRepository repository;
  private ABTestMapper mapper;

  @Transactional
  @Override
  public ABTest createABTest(ABTestRequestDto dto) throws DeploymentNotFoundException {
    // TODO: Manage wildcard subdomain ingress programmatically using route 53 & external-dns & fabric8 kubernetes client
    ABTest entity = repository.save(mapper.toABTest(dto));

    kubernetesService.deployABTest(entity);

    return entity;
  }

  @Transactional
  @Override
  public ABTest updateABTest(ABTestRequestDto dto)
      throws ABTestNotFoundException, DeploymentNotFoundException {
    ABTest entity = repository.findByName(dto.getName())
        .orElseThrow(() -> new ABTestNotFoundException("주어진 Name과 일치하는 AB Test 객체를 찾지 못했습니다."));

    Deployment a = deploymentService.findById(dto.getAId()).orElseThrow(
        () -> new DeploymentNotFoundException("주어진 Deployment A id와 일치하는 객체를 찾지 못했습니다."));
    Deployment b = deploymentService.findById(dto.getBId()).orElseThrow(
        () -> new DeploymentNotFoundException("주어진 Deployment A id와 일치하는 객체를 찾지 못했습니다."));

    entity.update(a, b, dto.getDomain());

    kubernetesService.deployABTest(entity);

    return entity;
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
