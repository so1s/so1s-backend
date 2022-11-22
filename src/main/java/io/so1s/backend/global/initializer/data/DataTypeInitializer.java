package io.so1s.backend.global.initializer.data;

import io.so1s.backend.domain.model.entity.DataType;
import io.so1s.backend.domain.model.repository.DataTypeRepository;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataTypeInitializer {

  private final DataTypeRepository repository;
  private static final List<String> names =
      // https://stackoverflow.com/a/7899558/11853111
      Arrays.asList("numpy json text file multipart image dataframe series".split("\\s+"));

  @PostConstruct
  private void addDeploymentStrategies() {
    names.forEach(name -> repository.findByName(name)
        .orElseGet(() -> repository.save(
            DataType.builder()
                .name(name)
                .build())));
  }
}
