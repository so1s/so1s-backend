package io.so1s.backend.domain.deployment.repository;

import io.so1s.backend.domain.deployment.entity.Deployment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeploymentRepository extends JpaRepository<Deployment, Long> {

  Deployment save(Deployment deployment);

  Optional<Deployment> findById(Long id);

  List<Deployment> findAll();

}
