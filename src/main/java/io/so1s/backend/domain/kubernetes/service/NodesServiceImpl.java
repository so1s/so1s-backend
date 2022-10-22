package io.so1s.backend.domain.kubernetes.service;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.List;
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

}
