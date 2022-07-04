package io.so1s.backend.domain.model.controller;

import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
import io.so1s.backend.domain.model.service.ModelService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class ModelController {

  private final ModelService modelService;

  @PostMapping
  public ResponseEntity<ModelUploadResponseDto> upload(
      @Valid ModelUploadRequestDto modelUploadRequestDto) {

    ModelUploadResponseDto result = modelService.save(modelUploadRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }
}
