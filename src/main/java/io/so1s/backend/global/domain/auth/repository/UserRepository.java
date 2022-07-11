package io.so1s.backend.global.domain.auth.repository;

import io.so1s.backend.global.domain.auth.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);
  
}
