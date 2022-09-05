package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelDeleteResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelDetailResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelFindResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataDeleteResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataFindResponseDto;
import io.so1s.backend.domain.model.entity.Library;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.global.error.exception.DeploymentExistsException;
import io.so1s.backend.global.error.exception.LibraryNotFoundException;
import io.so1s.backend.global.error.exception.ModelMetadataNotFoundException;
import io.so1s.backend.global.error.exception.ModelNotFoundException;
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

  ModelDeleteResponseDto deleteModel(Long modelId) throws ModelNotFoundException;

  ModelMetadataDeleteResponseDto deleteModelMetadata(Long modelId, String version)
      throws ModelMetadataNotFoundException, DeploymentExistsException;
}
