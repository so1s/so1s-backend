package io.so1s.backend.domain.model.dto.mapper;

import io.so1s.backend.domain.library.entity.Library;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelDetailResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelFindResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.registry.dto.mapper.RegistryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModelMapper {

  private final RegistryMapper registryMapper;

  @Transactional(readOnly = true)
  public ModelDetailResponseDto toDetailResponseDto(Model model, ModelMetadata modelMetadata) {
    var registry = modelMetadata.getRegistry();

    return ModelDetailResponseDto.builder()
        .age(modelMetadata.getUpdatedOn())
        .name(model.getName())
        .version(modelMetadata.getVersion())
        .status(modelMetadata.getStatus())
        .url(modelMetadata.getUrl())
        .registry(registryMapper.toStringFormat(registry))
        .library(model.getLibrary().getName())
        .inputShape(modelMetadata.getInputShape())
        .inputDtype(modelMetadata.getInputDtype())
        .outputShape(modelMetadata.getOutputShape())
        .outputDtype(modelMetadata.getOutputDtype())
        .build();
  }

  public ModelFindResponseDto toFindResponseDto(Model model, ModelMetadata modelMetadata) {
    return ModelFindResponseDto.builder()
        .id(model.getId())
        .age(model.getUpdatedOn())
        .name(model.getName())
        .status(modelMetadata.getStatus())
        .version(modelMetadata.getVersion())
        .library(model.getLibrary().getName())
        .build();
  }

  public Model toEntity(ModelUploadRequestDto uploadRequestDto, Library library) {
    return Model.builder()
        .name(uploadRequestDto.getName())
        .library(library)
        .build();
  }
}
