package io.so1s.backend.domain.deployment.service;

import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.request.ResourceRequestDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.Resource;

public interface DeploymentService {

  Resource createResource(ResourceRequestDto resourceRequestDto);

  Deployment createDeployment(Resource resource, DeploymentRequestDto deploymentRequestDto);

}
