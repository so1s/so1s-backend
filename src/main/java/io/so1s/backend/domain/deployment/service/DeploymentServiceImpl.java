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

    ModelMetadata modelMetadata = validateExistModelMetadata(
        deploymentRequestDto.getModelMetadataId());
    DeploymentStrategy deploymentStrategy = validateExistDeploymentStrategy(
        deploymentRequestDto.getStrategy());

    Deployment deployment = Deployment.builder()
        .name(deploymentRequestDto.getName())
        .status("pending")
        .build();
    deployment.setModelMetadata(modelMetadata);
    deployment.setDeploymentStrategy(deploymentStrategy);
    deployment.setResource(resource);

    return deploymentRepository.save(deployment);
  }

  @Override
  public ModelMetadata validateExistModelMetadata(Long id) {
    Optional<ModelMetadata> modelMetadata = modelMetadataRepository.findById(id);
    if (!modelMetadata.isPresent()) {
      throw new IllegalArgumentException(
          String.format("잘못된 모델버전을 선택했습니다. (%s)", id));
    }

    return modelMetadata.get();
  }

  @Override
  public DeploymentStrategy validateExistDeploymentStrategy(String name) {
    Optional<DeploymentStrategy> deploymentStrategy = deploymentStrategyRepository.findByName(name);
    if (!deploymentStrategy.isPresent()) {
      throw new IllegalArgumentException(
          String.format("잘못된 배포 전략을 선택하셨습니다. (%s)", name));
    }

    return deploymentStrategy.get();
  }
}
