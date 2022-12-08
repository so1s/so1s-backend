package io.so1s.backend.global.initializer.common;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KubernetesInitializer {

  private final KubernetesClient client;

  public boolean checkKubernetesConnection() {
    try {
      client.getVersion();
    } catch (KubernetesClientException e) {
      return false;
    }
    return true;
  }
}
