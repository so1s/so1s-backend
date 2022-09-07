package io.so1s.backend.domain.model.controller;

import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.aws.service.FileUploadService;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelDeleteResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelDetailResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelFindResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataDeleteResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataFindResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.exception.DuplicateModelNameException;
import io.so1s.backend.domain.model.exception.ModelNotFoundException;
import io.so1s.backend.domain.model.service.ModelService;
import io.so1s.backend.global.error.exception.DeploymentExistsException;
import io.so1s.backend.global.error.exception.DuplicateModelNameException;
import io.so1s.backend.global.error.exception.ModelMetadataNotFoundException;
import io.so1s.backend.global.error.exception.ModelNotFoundException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
      throws IllegalAccessError, IllegalArgumentException, DuplicateModelNameException, InterruptedException {

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
      @Valid ModelUploadRequestDto modelUploadRequestDto) throws
      ModelNotFoundException, InterruptedException {

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

  @DeleteMapping("/{modelId}")
  public ResponseEntity<ModelDeleteResponseDto> deleteModel(@PathVariable("modelId") Long modelId)
      throws ModelNotFoundException {
    return ResponseEntity.ok(modelService.deleteModel(modelId));
  }

  @DeleteMapping("/{modelId}/versions/{version}")
  public ResponseEntity<ModelMetadataDeleteResponseDto> deleteModelMetadata(
      @PathVariable("modelId") Long modelId,
      @PathVariable("version") String version)
      throws ModelMetadataNotFoundException, DeploymentExistsException {
    return ResponseEntity.ok(modelService.deleteModelMetadata(modelId, version));
  }

  @GetMapping
  public ResponseEntity<List<ModelFindResponseDto>> findModels() {
    return ResponseEntity.ok(modelService.findModels());
  }

  @GetMapping("/{modelId}")
  public ResponseEntity<List<ModelMetadataFindResponseDto>> findModelMetadatas(
      @PathVariable("modelId") Long modelId) {
    return ResponseEntity.ok(modelService.findModelMetadatasByModelId(modelId));
  }

  @GetMapping("/{modelId}/versions/{version}")
  public ResponseEntity<ModelDetailResponseDto> findModelMetadata(
      @PathVariable("modelId") Long modelId, @PathVariable("version") String version) {
    return ResponseEntity.ok(modelService.findModelDetail(modelId, version));
  }
}
