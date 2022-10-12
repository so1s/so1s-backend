package io.so1s.backend.domain.deployment.dto.mapper;

import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentFindResponseDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.global.vo.Status;
import org.springframework.stereotype.Component;

@Component
public class DeploymentMapper {

  public DeploymentFindResponseDto toDto(Deployment deployment) {
    return DeploymentFindResponseDto.builder()
        .id(deployment.getId())
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
        .standard(deployment.getStandard())
        .standardValue(deployment.getStandardValue())
        .maxReplicas(deployment.getMaxReplicas())
        .minReplicas(deployment.getMinReplicas())
        .build();
  }

  public Deployment toEntity(DeploymentRequestDto deploymentRequestDto) {
    return Deployment.builder()
        .name(deploymentRequestDto.getName())
        .status(Status.PENDING)
        .endPoint("inference-" + deploymentRequestDto.getName().toLowerCase() + ".so1s.io")
        .standard(deploymentRequestDto.getScale().getStandard())
        .standardValue(deploymentRequestDto.getScale().getStandardValue())
        .minReplicas(deploymentRequestDto.getScale().getMinReplicas())
        .maxReplicas(deploymentRequestDto.getScale().getMaxReplicas())
        .build();
  }
}
