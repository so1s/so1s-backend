package io.so1s.backend.global.config;

import io.so1s.backend.domain.registry.entity.Registry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@TestConfiguration
public class RegistryDataConfig {

  @Autowired
  TextEncryptor textEncryptor;

  @Bean
  @Primary
  public Registry defaultRegistry() {
    return Registry.builder()
        .name("default")
        .baseUrl("ghcr.io")
        .username("username")
        .password(textEncryptor.encrypt("password"))
        .build();
  }

}
