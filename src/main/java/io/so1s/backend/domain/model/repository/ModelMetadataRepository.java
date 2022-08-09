package io.so1s.backend.domain.model.repository;

import io.so1s.backend.domain.model.entity.ModelMetadata;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ModelMetadataRepository extends JpaRepository<ModelMetadata, Long> {

  ModelMetadata save(ModelMetadata modelMetadata);

  Optional<ModelMetadata> findById(Long id);

  List<ModelMetadata> findAll();

  @Query("select m from Model m where m.id = :id")
  List<ModelMetadata> findByModelId(Long id);
}
