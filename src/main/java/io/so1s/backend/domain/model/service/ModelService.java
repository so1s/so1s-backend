package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.deployment.exception.LibraryNotFoundException;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelDetailResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelFindResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataFindResponseDto;
import io.so1s.backend.domain.model.entity.Library;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.exception.ModelNotFoundException;
import java.util.List;

public interface ModelService {

  void validateDuplicateModelName(String name);

  Model createModel(ModelUploadRequestDto modelUploadRequestDto);

  ModelMetadata createModelMetadata(Model model, ModelUploadRequestDto modelUploadRequestDto,
      FileSaveResultForm fileSaveResultForm);

  Library validateLibrary(String library) throws LibraryNotFoundException;

  Model findModelByName(String name) throws ModelNotFoundException;

  ModelMetadata validateExistModelMetadata(Long id);

  List<ModelFindResponseDto> findModels();

  List<ModelMetadataFindResponseDto> findModelMetadatasByModelId(Long id);

  ModelDetailResponseDto findModelDetail(Long modelId, String version);
}
