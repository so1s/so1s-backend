package io.so1s.backend.domain.kubernetes.controller;

import io.fabric8.kubernetes.api.model.Node;
import io.so1s.backend.domain.kubernetes.service.NodesService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/nodes")
@RequiredArgsConstructor
public class NodeController {

  private final NodesService nodesService;

  @GetMapping
  public ResponseEntity<List<Node>> getNodes() {
    return ResponseEntity.ok(nodesService.findNodes());
  }

}
