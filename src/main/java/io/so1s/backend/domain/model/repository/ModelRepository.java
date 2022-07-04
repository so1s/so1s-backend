package io.so1s.backend.domain.model.repository;

import io.so1s.backend.domain.model.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

  Model findByName(String name);
}
