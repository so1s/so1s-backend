package io.so1s.backend.domain.kubernetes.service;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.so1s.backend.domain.kubernetes.exception.NodeNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodesServiceImpl implements NodesService {

  private final KubernetesClient client;

  @Override
  public List<Node> findNodes() {
    return client.nodes().list().getItems();
  }

  @Override
  public Node findNodeByName(String name) throws NodeNotFoundException {
    return Optional.ofNullable(client.nodes().withName(name).get())
        .orElseThrow(() -> new NodeNotFoundException(
            String.format("Node %s not found.", name)));
  }

}
