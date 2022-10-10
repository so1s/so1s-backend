package io.so1s.backend.domain.library.service;

import io.so1s.backend.domain.library.dto.mapper.LibraryMapper;
import io.so1s.backend.domain.library.dto.response.LibraryFindResponseDto;
import io.so1s.backend.domain.library.repository.LibraryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {

  private final LibraryRepository libraryRepository;
  private final LibraryMapper libraryMapper;

  @Override
  public List<LibraryFindResponseDto> findAll() {
    return libraryRepository.findAll().stream().map(libraryMapper::toDto)
        .collect(Collectors.toList());
  }
}
