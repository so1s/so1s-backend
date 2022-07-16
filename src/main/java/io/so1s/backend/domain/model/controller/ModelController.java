package io.so1s.backend.domain.model.controller;

import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
import io.so1s.backend.domain.model.service.ModelService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class ModelController {

  private final ModelService modelService;

  @PostMapping
  public ResponseEntity<ModelUploadResponseDto> modelUpload(
      @Valid @RequestBody ModelUploadRequestDto modelUploadRequestDto) {
    return ResponseEntity.ok(modelService.upload(modelUploadRequestDto));
  }
}