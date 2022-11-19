package io.so1s.backend.domain.test.v2.repository;

import io.so1s.backend.domain.test.v2.entity.ABNTestElement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ABNTestElementRepository extends JpaRepository<ABNTestElement, Long> {

  Optional<ABNTestElement> findByDeployment_Id(Long id);


}
