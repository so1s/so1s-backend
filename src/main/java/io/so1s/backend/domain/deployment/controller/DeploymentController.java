package io.so1s.backend.domain.deployment.controller;

import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentResponseDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.Resource;
import io.so1s.backend.domain.deployment.service.DeploymentService;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/deployments")
@RequiredArgsConstructor
public class DeploymentController {

  private final DeploymentService deploymentService;
  private final KubernetesService kubernetesService;

  @PostMapping
  public ResponseEntity<DeploymentResponseDto> deploy(
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
}
