package io.so1s.backend.domain.deployment.service;

import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.request.ResourceRequestDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentFindResponseDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import io.so1s.backend.domain.deployment.entity.Resource;

import java.util.List;
import java.util.Optional;

public interface DeploymentService {

  Resource createResource(ResourceRequestDto resourceRequestDto);

  Deployment createDeployment(Resource resource, DeploymentRequestDto deploymentRequestDto);

  DeploymentStrategy validateExistDeploymentStrategy(String name);

  Deployment updateDeployment(DeploymentRequestDto deploymentRequestDto);

  Deployment validateExistDeployment(String name);

  DeploymentFindResponseDto setDeploymentFindResponseDto(Deployment deployment);

  List<DeploymentFindResponseDto> findDeployments();

  DeploymentFindResponseDto findDeployment(Long id);

  Optional<Deployment> findById(Long id);
}
