package io.so1s.backend.domain.model.repository;

import io.so1s.backend.domain.model.entity.DataType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataTypeRepository extends JpaRepository<DataType, Long> {

  Optional<DataType> findByName(String name);

}
