package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelDetailResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelFindResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataFindResponseDto;
import io.so1s.backend.domain.model.entity.Library;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.LibraryRepository;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.global.entity.Status;
import io.so1s.backend.global.error.exception.DuplicateModelNameException;
import io.so1s.backend.global.error.exception.LibraryNotFoundException;
import io.so1s.backend.global.error.exception.ModelMetadataNotFoundException;
import io.so1s.backend.global.error.exception.ModelNotFoundException;
import io.so1s.backend.global.utils.HashGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

  private final ModelRepository modelRepository;
  private final LibraryRepository libraryRepository;
  private final ModelMetadataRepository modelMetadataRepository;

  @Transactional(readOnly = true)
  public void validateDuplicateModelName(String name) {
    Optional<Model> result = modelRepository.findByName(name);
    if (result.isPresent()) {
      throw new DuplicateModelNameException(
          String.format("중복된 모델명이 있습니다. (이름 : %s, 생성시간 : %s)",
              result.get().getName(),
              result.get().getUpdatedOn()));
    }
  }

  @Transactional
  public Model createModel(ModelUploadRequestDto modelUploadRequestDto) {
    validateDuplicateModelName(modelUploadRequestDto.getName());
    Library library = validateLibrary(modelUploadRequestDto.getLibrary());
    return modelRepository.save(Model.builder()
        .name(modelUploadRequestDto.getName())
        .library(library)
        .build());
  }

  @Transactional
  public ModelMetadata createModelMetadata(Model model,
      ModelUploadRequestDto modelUploadRequestDto, FileSaveResultForm fileSaveResultForm) {
    return modelMetadataRepository.save(ModelMetadata.builder()
        .status(Status.PENDING)
        .version(HashGenerator.sha256())
        .fileName(fileSaveResultForm.getSavedName())
        .url(fileSaveResultForm.getUrl())
        .inputShape(modelUploadRequestDto.getInputShape())
        .inputDtype(modelUploadRequestDto.getInputDtype())
        .outputShape(modelUploadRequestDto.getOutputShape())
        .outputDtype(modelUploadRequestDto.getOutputDtype())
        .model(model)
        .build());
  }

  @Transactional(readOnly = true)
  public Library validateLibrary(String library) throws LibraryNotFoundException {
    Optional<Library> result = libraryRepository.findByName(library);
    if (result.isEmpty()) {
      throw new LibraryNotFoundException(String.format("잘못된 라이브러리를 요청하셨습니다. (%s)", library));
    }

    return result.get();
  }

  @Transactional(readOnly = true)
  public Model findModelByName(String name) throws ModelNotFoundException {

    Optional<Model> model = modelRepository.findByName(name);
    if (model.isEmpty()) {
      throw new ModelNotFoundException(String.format("해당 모델을 찾을 수 없습니다.(%s)", name));
    }

    return model.get();
  }


  @Override
  public ModelMetadata validateExistModelMetadata(Long id) throws ModelMetadataNotFoundException {
    Optional<ModelMetadata> modelMetadata = modelMetadataRepository.findById(id);
    if (modelMetadata.isEmpty()) {
      throw new ModelMetadataNotFoundException(
          String.format("잘못된 모델버전을 선택했습니다. (%s)", id));
    }

    return modelMetadata.get();
  }

  @Transactional(readOnly = true)
  @Override
  public List<ModelFindResponseDto> findModels() {
    List<Model> models = modelRepository.findAll();
    List<ModelFindResponseDto> res = new ArrayList<>();
    for (Model m : models) {
      Optional<ModelMetadata> modelMetadata = modelMetadataRepository.findFirstByModelIdOrderByIdDesc(
          m.getId());

      modelMetadata.ifPresent(metadata -> res.add(ModelFindResponseDto.builder()
          .age(m.getUpdatedOn())
          .name(m.getName())
          .status(metadata.getStatus())
          .version(metadata.getVersion())
          .library(m.getLibrary().getName())
          .build()));
    }

    return res;
  }

  @Transactional(readOnly = true)
  @Override
  public List<ModelMetadataFindResponseDto> findModelMetadatasByModelId(Long id) {
    List<ModelMetadata> modelMetadatas = modelMetadataRepository.findByModelId(id);
    List<ModelMetadataFindResponseDto> res = new ArrayList<>();
    for (ModelMetadata mm : modelMetadatas) {
      res.add(ModelMetadataFindResponseDto.builder()
          .age(mm.getUpdatedOn())
          .version(mm.getVersion())
          .status(mm.getStatus())
          .url(mm.getUrl())
          .build());
    }

    return res;
  }

  @Transactional(readOnly = true)
  @Override
  public ModelDetailResponseDto findModelDetail(Long modelId, String version) {

    Optional<Model> model = modelRepository.findById(modelId);
    if (model.isEmpty()) {
      throw new ModelNotFoundException(
          String.format("모델을 찾을 수 없습니다. (%s, %s)", modelId, version));
    }

    Optional<ModelMetadata> modelMetadata = modelMetadataRepository.findByModelIdAndVersion(
        modelId, version);
    if (modelMetadata.isEmpty()) {
      throw new ModelMetadataNotFoundException(
          String.format("해당 버전의 모델을 찾을 수 없습니다. (%s, %s)", modelId, version));
    }

    return ModelDetailResponseDto.builder()
        .age(modelMetadata.get().getUpdatedOn())
        .name(model.get().getName())
        .version(modelMetadata.get().getVersion())
        .status(modelMetadata.get().getStatus())
        .url(modelMetadata.get().getUrl())
        .library(model.get().getLibrary().getName())
        .inputShape(modelMetadata.get().getInputShape())
        .inputDtype(modelMetadata.get().getInputDtype())
        .outputShape(modelMetadata.get().getOutputShape())
        .outputDtype(modelMetadata.get().getOutputDtype())
        .build();
  }
}
