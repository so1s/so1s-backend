package io.so1s.backend.domain.resource.service;

import io.so1s.backend.domain.resource.dto.mapper.ResourceMapper;
import io.so1s.backend.domain.resource.dto.request.ResourceCreateRequestDto;
import io.so1s.backend.domain.resource.dto.response.ResourceDeleteResponseDto;
import io.so1s.backend.domain.resource.dto.response.ResourceFindResponseDto;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.exception.ResourceNotFoundException;
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
  public Resource createResource(ResourceCreateRequestDto resourceRequestDto) {
    return resourceRepository.save(resourceMapper.toEntity(resourceRequestDto));
  }

  @Override
  public List<ResourceFindResponseDto> findAll() {
    return resourceRepository.findAll().stream().map(resourceMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public Resource findById(Long id) throws ResourceNotFoundException {
    return resourceRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("id와 일치하는 Resource가 존재하지 않습니다."));
  }

  @Override
  public ResourceDeleteResponseDto deleteResource(Long id) {
    Resource resource;
    try {
      resource = findById(id);
    } catch (ResourceNotFoundException ignored) {
      return ResourceDeleteResponseDto.builder()
          .success(false)
          .message("id와 일치하는 Resource가 존재하지 않습니다.").build();
    }

    resourceRepository.delete(resource);

    return ResourceDeleteResponseDto.builder()
        .success(true)
        .message("삭제가 완료되었습니다.").build();
  }
}
