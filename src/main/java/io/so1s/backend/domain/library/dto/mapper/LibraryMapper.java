package io.so1s.backend.domain.library.dto.mapper;

import io.so1s.backend.domain.library.dto.response.LibraryFindResponseDto;
import io.so1s.backend.domain.library.entity.Library;
import org.springframework.stereotype.Component;

@Component
public class LibraryMapper {

  public LibraryFindResponseDto toDto(Library entity) {
    return LibraryFindResponseDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .build();
  }
}
