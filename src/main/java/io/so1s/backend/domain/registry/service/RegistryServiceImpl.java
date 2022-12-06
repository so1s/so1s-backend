package io.so1s.backend.domain.registry.service;

import io.so1s.backend.domain.registry.dto.mapper.RegistryMapper;
import io.so1s.backend.domain.registry.dto.request.RegistryUploadRequestDto;
import io.so1s.backend.domain.registry.entity.Registry;
import io.so1s.backend.domain.registry.repository.RegistryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistryServiceImpl implements RegistryService {

  private final RegistryRepository repository;
  private final RegistryMapper mapper;

  @Override
  public List<Registry> findAll() {
    return repository.findAll();
  }

  @Override
  public Registry saveRegistry(RegistryUploadRequestDto requestDto) {
    return repository.save(mapper.toEntity(requestDto));
  }

}
