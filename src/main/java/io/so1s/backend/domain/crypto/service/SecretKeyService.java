package io.so1s.backend.domain.crypto.service;

public interface SecretKeyService {

  String decode(String encoded);

  String encode(String decoded);
}
