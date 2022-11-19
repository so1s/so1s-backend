package io.so1s.backend.domain.deployment.service;

import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentDeleteResponseDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentFindResponseDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentResponseDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.deployment.exception.DeploymentUpdateFailedException;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.test.v1.exception.ABTestExistsException;
import io.so1s.backend.global.error.exception.NodeResourceExceededException;
import java.util.List;
import java.util.Optional;

public interface DeploymentService {


  Deployment createDeployment(Resource resource, DeploymentRequestDto deploymentRequestDto)
      throws NodeResourceExceededException;

  DeploymentDeleteResponseDto deleteDeployment(Long id)
      throws DeploymentNotFoundException, ABTestExistsException;

  DeploymentResponseDto updateDeployment(DeploymentRequestDto deploymentRequestDto);

  boolean updateInference(Deployment deployment) throws DeploymentUpdateFailedException;

  Deployment validateExistDeployment(String name);

  List<DeploymentFindResponseDto> findDeployments();

  DeploymentFindResponseDto findDeployment(Long id);

  Optional<Deployment> findById(Long id);
}
