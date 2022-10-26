package io.so1s.backend.domain.deployment.controller;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentDeleteResponseDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentFindResponseDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentFindYamlResponseDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentResponseDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.deployment.exception.DeploymentUpdateFailedException;
import io.so1s.backend.domain.deployment.service.DeploymentService;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.service.ResourceService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/deployments")
@RequiredArgsConstructor
public class DeploymentController {

  private final DeploymentService deploymentService;
  private final KubernetesService kubernetesService;
  private final ResourceService resourceService;

  @PostMapping
  public ResponseEntity<DeploymentResponseDto> createDeployment(
      @Valid @RequestBody DeploymentRequestDto deploymentRequestDto) {

    Resource resource = resourceService.findById(deploymentRequestDto.getResourceId());
    Deployment deployment = deploymentService.createDeployment(resource, deploymentRequestDto);

    return ResponseEntity.ok(
        DeploymentResponseDto.builder()
            .success(kubernetesService.deployInferenceServer(deployment))
            .id(deployment.getId())
            .name(deployment.getName())
            .build());
  }

  @DeleteMapping("/{deployment_id}")
  public ResponseEntity<DeploymentDeleteResponseDto> deleteDeployment(
      @Valid @PathVariable("deployment_id") Long id)
      throws DeploymentNotFoundException {
    DeploymentDeleteResponseDto responseDto = deploymentService.deleteDeployment(id);

    return ResponseEntity.status(responseDto.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
        .body(responseDto);
  }

  @PutMapping
  public ResponseEntity<DeploymentResponseDto> updateDeployment(
      @Valid @RequestBody DeploymentRequestDto deploymentRequestDto)
      throws DeploymentNotFoundException, DeploymentUpdateFailedException {

    return ResponseEntity.ok(deploymentService.updateDeployment(deploymentRequestDto));
  }
  
  @GetMapping
  public ResponseEntity<List<DeploymentFindResponseDto>> findDeployments() {
    return ResponseEntity.ok(deploymentService.findDeployments());
  }

  @GetMapping("/{deployment_id}")
  public ResponseEntity<DeploymentFindResponseDto> findDeployment(
      @Valid @PathVariable("deployment_id") Long id
  ) throws DeploymentNotFoundException {
    return ResponseEntity.ok(deploymentService.findDeployment(id));
  }

  @GetMapping("/{deployment_id}/yaml")
  public ResponseEntity<DeploymentFindYamlResponseDto> findDeploymentYaml(
      @Valid @PathVariable("deployment_id") Long id) {
    String deploymentName = deploymentService.findDeployment(id).getDeploymentName().toLowerCase();
    HasMetadata deployment = kubernetesService.getDeploymentObject(deploymentName);
    String yaml = kubernetesService.getWorkloadToYaml(deployment);

    return ResponseEntity.ok(DeploymentFindYamlResponseDto.builder().yaml(yaml).build());
  }
}
