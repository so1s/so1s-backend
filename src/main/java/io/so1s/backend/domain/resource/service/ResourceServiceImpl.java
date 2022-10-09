package io.so1s.backend.domain.resource.service;

import io.so1s.backend.domain.deployment.dto.request.ResourceRequestDto;
import io.so1s.backend.domain.resource.dto.mapper.ResourceMapper;
import io.so1s.backend.domain.resource.dto.response.ResourceFindResponseDto;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.repository.ResourceRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

  private final ResourceRepository resourceRepository;
  private final ResourceMapper resourceMapper;

  @Override
  @Transactional
  public Resource createResource(ResourceRequestDto resourceRequestDto) {
    return resourceRepository.save(resourceRequestDto.toEntity());
  }

  @Override
  public List<ResourceFindResponseDto> findAll() {
    return resourceRepository.findAll().stream().map(resourceMapper::toDto)
        .collect(Collectors.toList());
  }
}
