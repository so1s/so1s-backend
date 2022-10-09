package io.so1s.backend.domain.deployment.service;

import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.request.ResourceRequestDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentDeleteResponseDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentFindResponseDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import io.so1s.backend.domain.deployment.entity.Resource;
import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.test.exception.ABTestExistsException;
import java.util.List;
import java.util.Optional;

public interface DeploymentService {

  Resource createResource(ResourceRequestDto resourceRequestDto);

  Deployment createDeployment(Resource resource, DeploymentRequestDto deploymentRequestDto);

  DeploymentDeleteResponseDto deleteDeployment(Long id)
      throws DeploymentNotFoundException, ABTestExistsException;

  DeploymentStrategy validateExistDeploymentStrategy(String name);

  Deployment updateDeployment(DeploymentRequestDto deploymentRequestDto);

  Deployment validateExistDeployment(String name);
  
  List<DeploymentFindResponseDto> findDeployments();

  DeploymentFindResponseDto findDeployment(Long id);

  Optional<Deployment> findById(Long id);
}
