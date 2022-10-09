package io.so1s.backend.domain.model.dto.mapper;

import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataFindResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.global.utils.HashGenerator;
import io.so1s.backend.global.vo.Status;
import org.springframework.stereotype.Component;

@Component
public class ModelMetadataMapper {

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
    return ModelMetadata.builder()
        .status(Status.PENDING)
        .version(HashGenerator.sha256())
        .fileName(fileSaveResultForm.getSavedName())
        .url(fileSaveResultForm.getUrl())
        .inputShape(modelUploadRequestDto.getInputShape())
        .inputDtype(modelUploadRequestDto.getInputDtype())
        .outputShape(modelUploadRequestDto.getOutputShape())
        .outputDtype(modelUploadRequestDto.getOutputDtype())
        .model(model)
        .build();
  }
}
