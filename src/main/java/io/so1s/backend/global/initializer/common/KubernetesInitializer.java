package io.so1s.backend.global.initializer.common;

import io.fabric8.kubernetes.client.KubernetesClient;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KubernetesInitializer {

  private final KubernetesClient client;

  @PostConstruct
  public void checkKubernetesConnection() {
    client.getVersion();
  }
}
