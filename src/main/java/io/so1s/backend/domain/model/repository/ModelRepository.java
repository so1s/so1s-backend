package io.so1s.backend.domain.model.repository;

import io.so1s.backend.domain.model.entity.Model;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, Long> {

  Model save(Model model);

  Optional<Model> findById(Long id);

  List<Model> findAll();

  Optional<Model> findByName(String name);

}
