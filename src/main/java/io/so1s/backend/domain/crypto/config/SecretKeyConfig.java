package io.so1s.backend.domain.crypto.config;

import io.so1s.backend.domain.crypto.entity.SecretKey;
import io.so1s.backend.domain.crypto.repository.SecretKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

@Configuration
@RequiredArgsConstructor
public class SecretKeyConfig {

  private final SecretKeyRepository repository;

  @Primary
  @Bean
  public SecretKey defaultSecretKey() {
    return repository.findByName("default").orElseGet(() ->
        repository.save(SecretKey.builder()
            .name("default")
            .key(KeyGenerators.string().generateKey())
            .salt(KeyGenerators.string().generateKey())
            .build())
    );
  }

  @Primary
  @Bean
  public TextEncryptor defaultTextEncryptor() {
    SecretKey secretKey = defaultSecretKey();

    return Encryptors.text(secretKey.getKey(), secretKey.getSalt());
  }

}
