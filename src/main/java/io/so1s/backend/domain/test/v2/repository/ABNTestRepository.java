package io.so1s.backend.domain.test.v2.repository;

import io.so1s.backend.domain.test.v2.entity.ABNTest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ABNTestRepository extends JpaRepository<ABNTest, Long> {

  Optional<ABNTest> findByName(String name);


}
