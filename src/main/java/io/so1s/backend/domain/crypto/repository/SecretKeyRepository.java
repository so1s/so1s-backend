package io.so1s.backend.domain.crypto.repository;

import io.so1s.backend.domain.crypto.entity.SecretKey;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecretKeyRepository extends JpaRepository<SecretKey, Long> {

  Optional<SecretKey> findByName(String name);

}
