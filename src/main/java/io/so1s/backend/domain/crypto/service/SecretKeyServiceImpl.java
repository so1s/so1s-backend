package io.so1s.backend.domain.crypto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecretKeyServiceImpl implements SecretKeyService {

  private final TextEncryptor encryptor;

  @Override
  public String decode(String encoded) {
    return encryptor.decrypt(encoded);
  }

  @Override
  public String encode(String decoded) {
    return encryptor.encrypt(decoded);
  }
}
