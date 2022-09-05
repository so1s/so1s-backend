package io.so1s.backend.domain.test.repository;

import io.so1s.backend.domain.test.entity.ABTest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ABTestRepository extends JpaRepository<ABTest, Long> {

  Optional<ABTest> findByName(String name);

  List<ABTest> findAllByA_Id(Long aId);

  List<ABTest> findAllByB_Id(Long bId);

}
