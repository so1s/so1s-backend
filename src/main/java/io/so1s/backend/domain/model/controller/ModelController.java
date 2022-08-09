package io.so1s.backend.domain.model.controller;

import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.aws.service.FileUploadService;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelFindResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataFindResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.service.ModelService;
import io.so1s.backend.global.error.exception.DuplicateModelNameException;
import io.so1s.backend.global.error.exception.ModelMetadataNotFoundException;
import io.so1s.backend.global.error.exception.ModelNotFoundException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  private final ModelMetadataRepository modelMetadataRepository;

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
  public ResponseEntity<List<ModelFindResponseDto>> findModels() {
    return ResponseEntity.ok(modelService.findModels());
  }

  @GetMapping("/{modelId}")
  public ResponseEntity<List<ModelMetadataFindResponseDto>> findModelMetadatas(
      @PathVariable("modelId") Long modelId) {
    return ResponseEntity.ok(modelService.findModelMetadatasByModelId(modelId));
  }

  @GetMapping("/{modelId}/versions/{version}")
  public ResponseEntity<ModelMetadataFindResponseDto> findModelMetadata(
      @PathVariable("modelId") Long modelId, @PathVariable("version") String version) {
    Optional<ModelMetadata> find = modelMetadataRepository.findByModelIdAndVersion(
        modelId, version);
    if (!find.isPresent()) {
      throw new ModelMetadataNotFoundException(
          String.format("모델을 찾을 수 없습니다. (%s, %s)", modelId, version));
    }

    return ResponseEntity.ok(ModelMetadataFindResponseDto.builder()
        .age(find.get().getCreatedOn())
        .version(find.get().getStatus())
        .status(find.get().getStatus())
        .url(find.get().getUrl())
        .build());
  }
}
