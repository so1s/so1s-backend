package io.so1s.backend.domain.resource.service;

import io.so1s.backend.domain.resource.dto.request.ResourceCreateRequestDto;
import io.so1s.backend.domain.resource.dto.response.ResourceDeleteResponseDto;
import io.so1s.backend.domain.resource.dto.response.ResourceFindResponseDto;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.exception.ResourceNotFoundException;
import java.util.List;

public interface ResourceService {

  Resource createResource(ResourceCreateRequestDto resourceRequestDto);

  List<ResourceFindResponseDto> findAll();

  Resource findById(Long id) throws ResourceNotFoundException;

  ResourceDeleteResponseDto deleteResource(Long id);

}
