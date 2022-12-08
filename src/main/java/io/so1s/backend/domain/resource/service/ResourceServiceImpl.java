package io.so1s.backend.domain.resource.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Quantity;
import io.so1s.backend.domain.kubernetes.service.NodesService;
import io.so1s.backend.domain.resource.dto.mapper.ResourceMapper;
import io.so1s.backend.domain.resource.dto.request.ResourceCreateRequestDto;
import io.so1s.backend.domain.resource.dto.response.ResourceDeleteResponseDto;
import io.so1s.backend.domain.resource.dto.response.ResourceFindResponseDto;
import io.so1s.backend.domain.resource.dto.service.ResourceDto;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.exception.ResourceNotFoundException;
import io.so1s.backend.domain.resource.repository.ResourceRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

  private final NodesService nodesService;
  private final ResourceRepository resourceRepository;
  private final ResourceMapper resourceMapper;
  private final ObjectMapper objectMapper;
  TypeReference<Map<String, Quantity>> resourceMapTypeRef = new TypeReference<>() {
  };

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

  @Override
  public boolean gte(Quantity a, Quantity b) {
    return Quantity.getAmountInBytes(a)
        .compareTo(Quantity.getAmountInBytes(b)) >= 0;
  }

  @Override
  public boolean isDeployable(ResourceDto allocatable, ResourceDto desired) {

    Map<String, Quantity> allocatableMap = objectMapper.convertValue(allocatable,
        resourceMapTypeRef);
    Map<String, Quantity> desiredMap = objectMapper.convertValue(desired, resourceMapTypeRef);

    Set<String> keys = allocatableMap.keySet();

    return keys.stream()
        .allMatch(key -> gte(allocatableMap.get(key), desiredMap.get(key)));
  }

  @Override
  public boolean isDeployable(Resource resource) {
    ResourceDto desired = resourceMapper.toServiceDto(resource);

    var inferenceNodes = nodesService.findNodes()
        .stream()
        .filter(e -> e.getSpec().getTaints().stream()
            .anyMatch(t -> t.getKey().equals("kind") && t.getValue().equals("inference")))
        .collect(Collectors.toList());

    if (inferenceNodes.size() == 0) {
      // Terraform으로 구성된 클러스터가 아닌 자체 매니지드 환경

      return true;
    }

    return inferenceNodes.stream()
        .anyMatch(e -> {
          ResourceDto allocatable = resourceMapper.toServiceDto(e.getStatus().getAllocatable());

          return isDeployable(allocatable, desired);
        });
  }
}
