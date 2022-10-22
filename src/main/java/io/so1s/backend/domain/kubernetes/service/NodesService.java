package io.so1s.backend.domain.kubernetes.service;

import io.fabric8.kubernetes.api.model.Node;
import io.so1s.backend.domain.kubernetes.exception.NodeNotFoundException;
import java.util.List;

public interface NodesService {

  List<Node> findNodes();

  Node findNodeByName(String name) throws NodeNotFoundException;

}
