package io.so1s.backend.domain.registry.service;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.so1s.backend.domain.kubernetes.service.NamespaceService;
import io.so1s.backend.domain.registry.dto.mapper.RegistryMapper;
import io.so1s.backend.domain.registry.entity.Registry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistryKubernetesServiceImpl implements RegistryKubernetesService {

  private final KubernetesClient kubernetesClient;
  private final RegistryMapper registryMapper;
  private final NamespaceService namespaceService;

  @Override
  public void deployRegistrySecret(Registry registry) {
    String namespace = namespaceService.getNamespace();

    Secret secret = new SecretBuilder()
        .withType("kubernetes.io/dockerconfigjson")
        .withNewMetadata()
        .withName(registry.getName())
        .withNamespace(namespace)
        .endMetadata()
        .addToData(".dockerconfigjson", registryMapper.toJsonEncoded(registry))
        .build();

    kubernetesClient.secrets().inNamespace(namespace).createOrReplace(secret);
  }
}
