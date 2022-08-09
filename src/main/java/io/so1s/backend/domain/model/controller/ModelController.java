package io.so1s.backend.domain.model.controller;

import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.aws.service.FileUploadService;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelFindResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.service.ModelService;
import io.so1s.backend.global.error.exception.DuplicateModelNameException;
import io.so1s.backend.global.error.exception.ModelNotFoundException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class ModelController {

  private final ModelService modelService;
  private final FileUploadService fileUploadService;
  private final KubernetesService kubernetesService;

  @PostMapping
  public ResponseEntity<ModelUploadResponseDto> modelUpload(
      @Valid ModelUploadRequestDto modelUploadRequestDto)
      throws IllegalAccessError, IllegalArgumentException, DuplicateModelNameException {

    Model model = modelService.createModel(modelUploadRequestDto);
    FileSaveResultForm saveResult = fileUploadService.uploadFile(
        modelUploadRequestDto.getModelFile());
    ModelMetadata modelMetadata = modelService.createModelMetadata(
        model, modelUploadRequestDto, saveResult);

    return ResponseEntity.ok(ModelUploadResponseDto.builder()
        .success(kubernetesService.inferenceServerBuild(modelMetadata))
        .modelName(model.getName())
        .version(modelMetadata.getVersion())
        .fileName(saveResult.getSavedName())
        .savedUrl(saveResult.getUrl())
        .build());
  }

  @PutMapping
  public ResponseEntity<ModelUploadResponseDto> modelUpdate(
      @Valid ModelUploadRequestDto modelUploadRequestDto) throws IllegalAccessError,
      IllegalArgumentException, ModelNotFoundException {

    Model model = modelService.findModelByName(modelUploadRequestDto.getName());
    FileSaveResultForm saveResult = fileUploadService.uploadFile(
        modelUploadRequestDto.getModelFile());
    ModelMetadata modelMetadata = modelService.createModelMetadata(model, modelUploadRequestDto,
        saveResult);

    return ResponseEntity.ok(ModelUploadResponseDto.builder()
        .success(kubernetesService.inferenceServerBuild(modelMetadata))
        .modelName(model.getName())
        .version(modelMetadata.getVersion())
        .fileName(saveResult.getSavedName())
        .savedUrl(saveResult.getUrl())
        .build());
  }

  @GetMapping
  public ResponseEntity<ModelFindResponseDto> findModels() {
    return ResponseEntity.ok(ModelFindResponseDto.builder()
        .models(modelService.findModels())
        .build());
  }
}
