package io.so1s.backend.domain.deployment.service;

import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.request.ResourceRequestDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentFindResponseDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import io.so1s.backend.domain.deployment.entity.Resource;
import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.deployment.exception.DeploymentStrategyNotFoundException;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.deployment.repository.DeploymentStrategyRepository;
import io.so1s.backend.domain.deployment.repository.ResourceRepository;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.service.ModelService;
import io.so1s.backend.global.vo.Status;
import java.util.ArrayList;
import java.util.List;
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
        .status(Status.PENDING)
        .endPoint("inference-" + deploymentRequestDto.getName() + "so1s.io")
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
          String.format("Invalid Deployment Strategy"));
    }

    return deploymentStrategy.get();
  }

  @Override
  public Deployment updateDeployment(DeploymentRequestDto deploymentRequestDto) {
    Deployment deployment = validateExistDeployment(deploymentRequestDto.getName());

    DeploymentStrategy deploymentStrategy = validateExistDeploymentStrategy(
        deploymentRequestDto.getStrategy());
    ModelMetadata modelMetadata = modelService.validateExistModelMetadata(
        deploymentRequestDto.getModelMetadataId());
    Resource resource = createResource(deploymentRequestDto.getResources());

    deployment.updateModel(modelMetadata, deploymentStrategy, resource);

    return deployment;
  }

  @Override
  public Deployment validateExistDeployment(String name) throws DeploymentNotFoundException {
    Optional<Deployment> result = deploymentRepository.findByName(name);
    if (!result.isPresent()) {
      throw new DeploymentNotFoundException(String.format("Not Found Deployment"));
    }

    return result.get();
  }

  @Override
  public DeploymentFindResponseDto setDeploymentFindResponseDto(Deployment deployment) {
    return DeploymentFindResponseDto.builder()
        .age(deployment.getUpdatedOn().toString())
        .deploymentName(deployment.getName())
        .status(deployment.getStatus())
        .endPoint(deployment.getEndPoint())
        .strategy(deployment.getDeploymentStrategy().getName())
        .modelName(deployment.getModelMetadata().getModel().getName())
        .modelVersion(deployment.getModelMetadata().getVersion())
        .cpu(deployment.getResource().getCpu())
        .memory(deployment.getResource().getMemory())
        .gpu(deployment.getResource().getGpu())
        .cpuLimit(deployment.getResource().getCpuLimit())
        .memoryLimit(deployment.getResource().getMemoryLimit())
        .gpuLimit(deployment.getResource().getGpuLimit())
        .build();
  }

  @Override
  public List<DeploymentFindResponseDto> findDeployments() {
    List<DeploymentFindResponseDto> list = new ArrayList<>();

    List<Deployment> findDeployments = deploymentRepository.findAll();
    for (Deployment d : findDeployments) {
      list.add(setDeploymentFindResponseDto(d));
    }

    return list;
  }

  @Override
  public DeploymentFindResponseDto findDeployment(Long id) throws DeploymentNotFoundException {
    Optional<Deployment> deployment = deploymentRepository.findById(id);
    if (deployment.isEmpty()) {
      throw new DeploymentNotFoundException(String.format("Not Found Deployment."));
    }

    return setDeploymentFindResponseDto(deployment.get());
  }

  @Override
  public Optional<Deployment> findById(Long id) {
    return deploymentRepository.findById(id);
  }
}
