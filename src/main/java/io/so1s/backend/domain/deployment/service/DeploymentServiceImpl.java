package io.so1s.backend.domain.deployment.service;

import io.so1s.backend.domain.deployment.dto.mapper.DeploymentMapper;
import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.request.ResourceRequestDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentDeleteResponseDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentFindResponseDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import io.so1s.backend.domain.deployment.entity.Resource;
import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.deployment.exception.DeploymentStrategyNotFoundException;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.deployment.repository.DeploymentStrategyRepository;
import io.so1s.backend.domain.deployment.repository.ResourceRepository;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.service.ModelService;
import io.so1s.backend.domain.test.entity.ABTest;
import io.so1s.backend.domain.test.exception.ABTestExistsException;
import io.so1s.backend.domain.test.repository.ABTestRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeploymentServiceImpl implements DeploymentService {

  private final DeploymentRepository deploymentRepository;
  private final DeploymentStrategyRepository deploymentStrategyRepository;
  private final ResourceRepository resourceRepository;
  private final ABTestRepository abTestRepository;
  private final KubernetesService kubernetesService;

  private final ModelService modelService;
  private final DeploymentMapper deploymentMapper;

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

    Deployment deployment = deploymentMapper.toEntity(deploymentRequestDto);

    deployment.setModelMetadata(modelMetadata);
    deployment.setDeploymentStrategy(deploymentStrategy);
    deployment.setResource(resource);

    return deploymentRepository.save(deployment);
  }

  @Override
  public DeploymentDeleteResponseDto deleteDeployment(Long id)
      throws DeploymentNotFoundException, ABTestExistsException {
    Deployment deployment = deploymentRepository.findById(id).orElseThrow(
        () -> new DeploymentNotFoundException(String.format("디플로이먼트 %s를 찾을 수 없습니다.", id)));

    List<ABTest> aTests = abTestRepository.findAllByA_Id(deployment.getId());
    List<ABTest> bTests = abTestRepository.findAllByB_Id(deployment.getId());

    if (!aTests.isEmpty() || !bTests.isEmpty()) {
      throw new ABTestExistsException("해당 디플로이먼트를 사용하고 있는 AB 테스트가 존재합니다.\nAB 테스트를 먼저 삭제해 주세요.");
    }

    boolean result = kubernetesService.deleteDeployment(deployment);

    if (!result) {
      return DeploymentDeleteResponseDto.builder()
          .success(false)
          .message("디플로이먼트 삭제에 실패했습니다.").build();
    }

    deploymentRepository.deleteById(deployment.getId());

    return DeploymentDeleteResponseDto.builder()
        .success(true)
        .message("디플로이먼트 삭제가 완료되었습니다.").build();
  }

  @Override
  public DeploymentStrategy validateExistDeploymentStrategy(String name) {
    Optional<DeploymentStrategy> deploymentStrategy = deploymentStrategyRepository.findByName(name);
    if (!deploymentStrategy.isPresent()) {
      throw new DeploymentStrategyNotFoundException("Invalid Deployment Strategy");
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
    return deploymentRepository.findByName(name)
        .orElseThrow(() -> new DeploymentNotFoundException("Deployment Not Found."));
  }

  @Override
  public List<DeploymentFindResponseDto> findDeployments() {
    return deploymentRepository.findAll()
        .stream()
        .map(deploymentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public DeploymentFindResponseDto findDeployment(Long id) throws DeploymentNotFoundException {
    return deploymentRepository.findById(id)
        .map(deploymentMapper::toDto)
        .orElseThrow(() -> new DeploymentNotFoundException("Deployment Not Found."));
  }

  @Override
  public Optional<Deployment> findById(Long id) {
    return deploymentRepository.findById(id);
  }
}
