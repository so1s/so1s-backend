package io.so1s.backend.domain.registry.service;

import io.fabric8.kubernetes.api.model.Secret;
import java.util.Optional;

public interface RegistryKubernetesService {

  Optional<Secret> getDefaultSecret();

}
