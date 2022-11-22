package io.so1s.backend.global.initializer.common;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KubernetesInitializer {

  private final KubernetesClient client;
  private final KubernetesService kubernetesService;

  @PostConstruct
  private void createNamespace() {
    if (checkKuberetesConnection()) {
      kubernetesService.createNamespace("so1s");
    }
  }

  public boolean checkKuberetesConnection() {
    try {
      client.getVersion();
      return true;
    } catch (KubernetesClientException e) {

    }
    return false;
  }
}
