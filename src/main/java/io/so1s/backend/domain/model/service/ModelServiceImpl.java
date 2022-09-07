package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.deployment.exception.LibraryNotFoundException;
import io.so1s.backend.domain.aws.service.AwsS3Service;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelDeleteResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelDetailResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelFindResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataDeleteResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataFindResponseDto;
import io.so1s.backend.domain.model.entity.Library;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.exception.DuplicateModelNameException;
import io.so1s.backend.domain.model.exception.ModelMetadataNotFoundException;
import io.so1s.backend.domain.model.exception.ModelNotFoundException;
import io.so1s.backend.domain.model.repository.LibraryRepository;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.global.entity.Status;
import io.so1s.backend.global.error.exception.DeploymentExistsException;
import io.so1s.backend.global.error.exception.DuplicateModelNameException;
import io.so1s.backend.global.error.exception.ModelMetadataExistsException;
import io.so1s.backend.global.error.exception.ModelMetadataNotFoundException;
import io.so1s.backend.global.error.exception.ModelNotFoundException;
import io.so1s.backend.global.utils.HashGenerator;
import io.so1s.backend.global.vo.Status;
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
  private final DeploymentRepository deploymentRepository;
  private final AwsS3Service awsS3Service;

  @Transactional(readOnly = true)
  public void validateDuplicateModelName(String name) {
    Optional<Model> result = modelRepository.findByName(name);
    if (result.isPresent()) {
      throw new DuplicateModelNameException(
          String.format("Model name is duplicated. (name : %s, created_at : %s)",
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
      throw new LibraryNotFoundException(String.format("Invalid library."));
    }

    return result.get();
  }

  @Transactional(readOnly = true)
  public Model findModelByName(String name) throws ModelNotFoundException {

    Optional<Model> model = modelRepository.findByName(name);
    if (model.isEmpty()) {
      throw new ModelNotFoundException(String.format("Model not found."));
    }

    return model.get();
  }


  @Override
  public ModelMetadata validateExistModelMetadata(Long id) throws ModelMetadataNotFoundException {
    Optional<ModelMetadata> modelMetadata = modelMetadataRepository.findById(id);
    if (modelMetadata.isEmpty()) {
      throw new ModelMetadataNotFoundException(
          String.format("Invalid model version."));
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
          String.format("Model not found."));
    }

    Optional<ModelMetadata> modelMetadata = modelMetadataRepository.findByModelIdAndVersion(
        modelId, version);
    if (modelMetadata.isEmpty()) {
      throw new ModelMetadataNotFoundException(
          String.format("Invalid model version."));
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

  @Override
  public ModelDeleteResponseDto deleteModel(Long modelId)
      throws ModelNotFoundException, ModelMetadataExistsException {
    Model model = modelRepository.findById(modelId)
        .orElseThrow(() -> new ModelNotFoundException("모델을 찾지 못했습니다."));

    List<ModelMetadata> modelMetadatas = modelMetadataRepository.findByModelId(modelId);

    if (!modelMetadatas.isEmpty()) {
      throw new ModelMetadataExistsException("모델을 사용하는 모델 메타데이터가 존재합니다.");
    }

    modelRepository.delete(model);

    return ModelDeleteResponseDto.builder()
        .success(true)
        .message("모델이 삭제되었습니다.")
        .build();
  }

  @Override
  public ModelMetadataDeleteResponseDto deleteModelMetadata(Long modelId, String version)
      throws ModelMetadataNotFoundException, DeploymentExistsException {
    ModelMetadata modelMetadata = modelMetadataRepository.findByModelIdAndVersion(modelId, version)
        .orElseThrow(() -> new ModelMetadataNotFoundException("모델 메타데이터를 찾지 못했습니다."));

    Optional<Deployment> deployment = deploymentRepository.findByModelMetadata(modelMetadata);

    if (deployment.isPresent()) {
      throw new DeploymentExistsException("모델 메타데이터를 사용하는 Deployment가 존재합니다.");
    }

    awsS3Service.deleteFile(modelMetadata.getUrl());

    modelMetadataRepository.delete(modelMetadata);

    return ModelMetadataDeleteResponseDto.builder()
        .success(true)
        .message("모델 메타데이터가 삭제되었습니다.")
        .build();
  }
}
