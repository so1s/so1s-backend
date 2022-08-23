package io.so1s.backend.domain.deployment.controller;

import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentFindResponseDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentResponseDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.Resource;
import io.so1s.backend.domain.deployment.service.DeploymentService;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.service.ModelService;
import io.so1s.backend.global.error.exception.DeploymentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/api/v1/deployments")
@RequiredArgsConstructor
public class DeploymentController {

  private final DeploymentService deploymentService;
  private final KubernetesService kubernetesService;
  private final ModelService modelService;

  @PostMapping
  public ResponseEntity<DeploymentResponseDto> createDeployment(
      @Valid @RequestBody DeploymentRequestDto deploymentRequestDto) {

    Resource resource = deploymentService.createResource(deploymentRequestDto.getResources());
    Deployment deployment = deploymentService.createDeployment(resource, deploymentRequestDto);

    return ResponseEntity.ok(
        DeploymentResponseDto.builder()
            .success(kubernetesService.deployInferenceServer(deployment))
            .id(deployment.getId())
            .name(deployment.getName())
            .build());
  }

  @PutMapping
  public ResponseEntity<DeploymentResponseDto> updateDeployment(
      @Valid @RequestBody DeploymentRequestDto deploymentRequestDto)
      throws DeploymentNotFoundException {

    Deployment deployment = deploymentService.updateDeployment(deploymentRequestDto);

    return ResponseEntity.ok(
        DeploymentResponseDto.builder()
            .success(kubernetesService.deployInferenceServer(deployment))
            .id(deployment.getId())
            .name(deployment.getName())
            .build());
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
}
