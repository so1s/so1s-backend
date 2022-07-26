package io.so1s.backend.domain.deployment.repository;

import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeploymentStrategyRepository extends JpaRepository<DeploymentStrategy, Long> {

  DeploymentStrategy save(DeploymentStrategy deploymentStrategy);

  Optional<DeploymentStrategy> findById(Long id);

  List<DeploymentStrategy> findAll();

  Optional<DeploymentStrategy> findByName(String name);

}
