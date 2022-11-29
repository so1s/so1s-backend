package io.so1s.backend.domain.registry.config;

import io.fabric8.kubernetes.api.model.Secret;
import io.so1s.backend.domain.registry.entity.Registry;
import io.so1s.backend.domain.registry.service.RegistryKubernetesService;
import io.so1s.backend.global.utils.Base64Mapper;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class RegistryConfig {

  private final RegistryKubernetesService service;

  // TODO: Spring data jpa integration
  @Primary
  @Bean
  public Registry defaultRegistry() throws IllegalStateException {

    Secret secret = service.getDefaultSecret().orElseThrow(
        () -> new IllegalStateException("Default registry secret was not found."));

    var data = secret.getData();

    String username = Optional.ofNullable(data.get("username")).map(Base64Mapper::decode).orElseThrow(
        () -> new IllegalStateException("password field in secret definition was not found."));
    String password = Optional.ofNullable(data.get("password")).map(Base64Mapper::decode).orElseThrow(
        () -> new IllegalStateException("password field in secret definition was not found."));

    return Registry.builder()
        .username(username)
        .password(password)
        .build();
  }
}
