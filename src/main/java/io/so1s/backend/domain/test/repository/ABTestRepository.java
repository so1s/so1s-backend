package io.so1s.backend.domain.test.repository;

import io.so1s.backend.domain.test.entity.ABTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ABTestRepository extends JpaRepository<ABTest, Long> {

  Optional<ABTest> findByName(String name);

}
