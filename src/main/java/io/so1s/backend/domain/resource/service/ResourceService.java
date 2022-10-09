package io.so1s.backend.domain.resource.service;

import io.so1s.backend.domain.deployment.dto.request.ResourceRequestDto;
import io.so1s.backend.domain.resource.dto.response.ResourceFindResponseDto;
import io.so1s.backend.domain.resource.entity.Resource;
import java.util.List;

public interface ResourceService {

  Resource createResource(ResourceRequestDto resourceRequestDto);

  List<ResourceFindResponseDto> findAll();

}
