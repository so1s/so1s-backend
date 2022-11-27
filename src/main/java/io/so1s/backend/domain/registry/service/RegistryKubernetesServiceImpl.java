package io.so1s.backend.domain.registry.service;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistryKubernetesServiceImpl implements RegistryKubernetesService {

  private final KubernetesClient kubernetesClient;

  @Override
  public Optional<Secret> getDefaultSecret() {
    return Optional.of(
        kubernetesClient.secrets().inNamespace("backend").withName("default-registry").get());
  }
}
