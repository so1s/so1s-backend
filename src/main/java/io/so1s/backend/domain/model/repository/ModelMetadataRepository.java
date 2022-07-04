package io.so1s.backend.domain.model.repository;

import io.so1s.backend.domain.model.entity.ModelMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelMetadataRepository extends JpaRepository<ModelMetadata, Long> {

}
