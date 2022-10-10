package io.so1s.backend.domain.resource.repository;

import io.so1s.backend.domain.resource.entity.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

  Resource save(Resource resource);

  Optional<Resource> findById(Long id);

  List<Resource> findAll();

}
