package io.so1s.backend.domain.model.repository;

import io.so1s.backend.domain.model.entity.Library;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<Library, Long> {

  Library save(Library library);

  Optional<Library> findById(Long id);

  List<Library> findAll();

  Optional<Library> findByName(String name);
}
