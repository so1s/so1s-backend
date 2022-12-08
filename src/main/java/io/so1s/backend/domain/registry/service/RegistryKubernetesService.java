package io.so1s.backend.domain.registry.service;

import io.so1s.backend.domain.registry.entity.Registry;

public interface RegistryKubernetesService {

  void deployRegistrySecret(Registry registry);

}
