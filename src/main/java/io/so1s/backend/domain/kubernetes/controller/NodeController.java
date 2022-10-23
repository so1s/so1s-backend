package io.so1s.backend.domain.kubernetes.controller;

import io.fabric8.kubernetes.api.model.Node;
import io.so1s.backend.domain.kubernetes.dto.response.NodeFindResponseDto;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.kubernetes.service.NodesService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/nodes")
@RequiredArgsConstructor
public class NodeController {

  private final NodesService nodesService;
  private final KubernetesService kubernetesService;

  @GetMapping
  public ResponseEntity<List<Node>> getNodes() {
    return ResponseEntity.ok(nodesService.findNodes());
  }

  @GetMapping("/{node_name}/yaml")
  public ResponseEntity<NodeFindResponseDto> findDeploymentYaml(
      @Valid @PathVariable("node_name") String nodeName) {
    Node node = nodesService.findNodeByName(nodeName);
    String yaml = kubernetesService.getWorkloadToYaml(node);

    return ResponseEntity.ok(NodeFindResponseDto.builder().yaml(yaml).build());
  }

}
