package io.so1s.backend.domain.library.service;

import io.so1s.backend.domain.library.dto.response.LibraryFindResponseDto;
import java.util.List;

public interface LibraryService {

  List<LibraryFindResponseDto> findAll();
}
