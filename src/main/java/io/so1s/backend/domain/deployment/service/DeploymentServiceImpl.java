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
import io.so1s.backend.domain.model.service.ModelService;
import io.so1s.backend.global.error.exception.DeploymentNotFoundException;
import io.so1s.backend.global.error.exception.DeploymentStrategyNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeploymentServiceImpl implements DeploymentService {

  private final DeploymentRepository deploymentRepository;
  private final DeploymentStrategyRepository deploymentStrategyRepository;
  private final ResourceRepository resourceRepository;

  private final ModelService modelService;

  @Override
  @Transactional
  public Resource createResource(ResourceRequestDto resourceRequestDto) {
    return resourceRepository.save(resourceRequestDto.toEntity());
  }

  @Override
  @Transactional
  public Deployment createDeployment(Resource resource, DeploymentRequestDto deploymentRequestDto) {

    ModelMetadata modelMetadata = modelService.validateExistModelMetadata(
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
  public DeploymentStrategy validateExistDeploymentStrategy(String name) {
    Optional<DeploymentStrategy> deploymentStrategy = deploymentStrategyRepository.findByName(name);
    if (!deploymentStrategy.isPresent()) {
      throw new DeploymentStrategyNotFoundException(
          String.format("잘못된 배포 전략을 선택하셨습니다. (%s)", name));
    }

    return deploymentStrategy.get();
  }

  @Override
  public Deployment validateExistDeployment(String name) throws DeploymentNotFoundException {
    Optional<Deployment> result = deploymentRepository.findByName(name);
    if (!result.isPresent()) {
      throw new DeploymentNotFoundException(String.format("디플로이먼트를 찾을 수 없습니다.(%s)", name));
    }

    return result.get();
  }
}
