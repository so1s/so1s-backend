package io.so1s.backend.domain.registry.repository;

import io.so1s.backend.domain.registry.entity.Registry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistryRepository extends JpaRepository<Registry, Long> {

}
