package io.so1s.backend.domain.registry.service;

import io.so1s.backend.domain.registry.dto.mapper.RegistryMapper;
import io.so1s.backend.domain.registry.dto.request.RegistryUploadRequestDto;
import io.so1s.backend.domain.registry.entity.Registry;
import io.so1s.backend.domain.registry.repository.RegistryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistryServiceImpl implements RegistryService {

  private final RegistryKubernetesService kubernetesService;
  private final RegistryRepository repository;
  private final RegistryMapper mapper;

  @Override
  public List<Registry> findAll() {
    return repository.findAll();
  }

  @Override
  public Registry saveRegistry(RegistryUploadRequestDto requestDto) {
    Registry registry = mapper.toEntity(requestDto);

    kubernetesService.deployRegistrySecret(registry);
    repository.save(registry);

    return registry;
  }

  @Override
  public Optional<Registry> findRegistryById(Long id) {
    return repository.findById(id);
  }

}
