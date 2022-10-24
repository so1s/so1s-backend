package io.so1s.backend.domain.resource.service;

import io.fabric8.kubernetes.api.model.Quantity;
import io.so1s.backend.domain.resource.dto.request.ResourceCreateRequestDto;
import io.so1s.backend.domain.resource.dto.response.ResourceDeleteResponseDto;
import io.so1s.backend.domain.resource.dto.response.ResourceFindResponseDto;
import io.so1s.backend.domain.resource.dto.service.ResourceDto;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.exception.ResourceNotFoundException;
import java.util.List;

public interface ResourceService {

  Resource createResource(ResourceCreateRequestDto resourceRequestDto);

  List<ResourceFindResponseDto> findAll();

  Resource findById(Long id) throws ResourceNotFoundException;

  ResourceDeleteResponseDto deleteResource(Long id);

  /**
   * Check that first Quantity is greater or equal (a >= b) to second one. Internally converted and
   * compared by BigDecimal bytes.
   *
   * @return true if a >= b or false if less than.
   */
  boolean gte(Quantity a, Quantity b);

  /**
   * Check that desired resource is deployable in specific node allocatable resource.
   *
   * @param allocatable the current allocatable resource status of node.
   * @param desired     the resource spec we desire to create deployment.
   * @return true if deployment available or false.
   */
  boolean isDeployable(ResourceDto allocatable, ResourceDto desired);

  /**
   * Check that desired resource is deployable at least one inference node.
   *
   * @param resource the resource spec we desire to create deployment.
   * @return true if deployment available or false.
   */
  boolean isDeployable(Resource resource);

}
