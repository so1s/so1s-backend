package io.so1s.backend.domain.model.dto.mapper;

import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataFindResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.registry.entity.Registry;
import io.so1s.backend.domain.registry.exception.RegistryNotFoundException;
import io.so1s.backend.domain.registry.service.RegistryService;
import io.so1s.backend.global.utils.HashGenerator;
import io.so1s.backend.global.vo.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelMetadataMapper {

  private final RegistryService registryService;

  public ModelMetadataFindResponseDto toFindResponseDto(ModelMetadata entity) {
    return ModelMetadataFindResponseDto.builder()
        .id(entity.getId())
        .age(entity.getUpdatedOn())
        .version(entity.getVersion())
        .status(entity.getStatus())
        .url(entity.getUrl())
        .build();
  }

  public ModelMetadata toEntity(Model model,
      ModelUploadRequestDto modelUploadRequestDto, FileSaveResultForm fileSaveResultForm) {
    Long registryId = modelUploadRequestDto.getRegistryId();
    Registry registry = registryService.findRegistryById(registryId)
        .orElseThrow(() -> new RegistryNotFoundException(
            String.format("Registry id %d not found.", registryId)));

    return ModelMetadata.builder()
        .status(Status.PENDING)
        .version(HashGenerator.sha256().toLowerCase())
        .fileName(fileSaveResultForm.getSavedName())
        .url(fileSaveResultForm.getUrl())
        .registry(registry)
        .inputShape(modelUploadRequestDto.getInputShape())
        .inputDtype(modelUploadRequestDto.getInputDtype())
        .outputShape(modelUploadRequestDto.getOutputShape())
        .outputDtype(modelUploadRequestDto.getOutputDtype())
        .deviceType(modelUploadRequestDto.getDeviceType())
        .model(model)
        .build();
  }
}
