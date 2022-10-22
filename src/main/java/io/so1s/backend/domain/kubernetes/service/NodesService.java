package io.so1s.backend.domain.kubernetes.service;

import io.fabric8.kubernetes.api.model.Node;
import java.util.List;

public interface NodesService {

  List<Node> findNodes();

}
