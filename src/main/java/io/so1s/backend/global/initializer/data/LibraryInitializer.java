package io.so1s.backend.global.initializer.data;

import io.so1s.backend.domain.library.entity.Library;
import io.so1s.backend.domain.library.repository.LibraryRepository;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LibraryInitializer {

  private final LibraryRepository repository;
  private static final List<String> names =
      // https://stackoverflow.com/a/7899558/11853111
      Arrays.asList(
          "tensorflow pytorch sklearn keras detectron2 transformers pytorch_lightning xgboost".split(
              "\\s+"));

  @PostConstruct
  private void addLibraries() {
    names.forEach(name -> repository.findByName(name)
        .orElseGet(() -> repository.save(
            Library.builder()
                .name(name)
                .build())));
  }
}
