package io.so1s.backend.domain.resource.controller;

import io.so1s.backend.domain.resource.dto.request.ResourceCreateRequestDto;
import io.so1s.backend.domain.resource.dto.response.ResourceCreateResponseDto;
import io.so1s.backend.domain.resource.dto.response.ResourceDeleteResponseDto;
import io.so1s.backend.domain.resource.dto.response.ResourceFindResponseDto;
import io.so1s.backend.domain.resource.service.ResourceService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourceController {

  private final ResourceService resourceService;

  @GetMapping
  public ResponseEntity<List<ResourceFindResponseDto>> getResources() {
    return ResponseEntity.ok(resourceService.findAll());
  }

  @PostMapping
  public ResponseEntity<ResourceCreateResponseDto> createResource(
      @Valid @RequestBody ResourceCreateRequestDto resourceRequestDto) {

    resourceService.createResource(resourceRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ResourceCreateResponseDto.builder()
            .success(true)
            .message(String.format("리소스 %s가 생성되었습니다.", resourceRequestDto.getName()))
            .build());
  }

  @DeleteMapping("/{resource_id}")
  public ResponseEntity<ResourceDeleteResponseDto> deleteResource(
      @Valid @PathVariable("resource_id") Long id) {
    ResourceDeleteResponseDto responseDto = resourceService.deleteResource(id);

    return ResponseEntity.status(responseDto.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
        .body(responseDto);
  }

}
