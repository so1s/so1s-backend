package io.so1s.backend.domain.deployment.service;

import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.request.ResourceRequestDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import io.so1s.backend.domain.deployment.entity.Resource;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.deployment.repository.DeploymentStrategyRepository;
import io.so1s.backend.domain.deployment.repository.ResourceRepository;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeploymentServiceImpl implements DeploymentService {

  private final ModelMetadataRepository modelMetadataRepository;
  private final DeploymentRepository deploymentRepository;
  private final DeploymentStrategyRepository deploymentStrategyRepository;
  private final ResourceRepository resourceRepository;

  @Override
  @Transactional
  public Resource createResource(ResourceRequestDto resourceRequestDto) {
    return resourceRepository.save(resourceRequestDto.toEntity());
  }

  @Override
  @Transactional
  public Deployment createDeployment(Resource resource, DeploymentRequestDto deploymentRequestDto) {
    Optional<ModelMetadata> modelMetadata = modelMetadataRepository.findById(
        deploymentRequestDto.getModelMetadataId());
    if (!modelMetadata.isPresent()) {
      throw new IllegalArgumentException(
          String.format("잘못된 모델을 선택했습니다. (%s)", deploymentRequestDto.getModelMetadataId()));
    }

    Optional<DeploymentStrategy> deploymentStrategy = deploymentStrategyRepository.findByName(
        deploymentRequestDto.getStrategy());
    if (!deploymentStrategy.isPresent()) {
      throw new IllegalArgumentException(
          String.format("잘못된 배포 전략입니다. (%s)", deploymentRequestDto.getStrategy()));
    }

    Deployment deployment = Deployment.builder()
        .name(deploymentRequestDto.getName())
        .status("pending")
        .build();
    deployment.setModelMetadata(modelMetadata.get());
    deployment.setDeploymentStrategy(deploymentStrategy.get());
    deployment.setResource(resource);

    return deploymentRepository.save(deployment);
  }
}
