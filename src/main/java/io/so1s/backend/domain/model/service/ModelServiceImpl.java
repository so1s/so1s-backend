package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.aws.service.AwsS3Service;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.exception.DeploymentExistsException;
import io.so1s.backend.domain.deployment.exception.LibraryNotFoundException;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.library.entity.Library;
import io.so1s.backend.domain.library.repository.LibraryRepository;
import io.so1s.backend.domain.model.dto.mapper.ModelMapper;
import io.so1s.backend.domain.model.dto.mapper.ModelMetadataMapper;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelDeleteResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelDetailResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelFindResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataDeleteResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataFindResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.exception.DataTypeNotFoundException;
import io.so1s.backend.domain.model.exception.DuplicatedModelNameException;
import io.so1s.backend.domain.model.exception.ModelMetadataExistsException;
import io.so1s.backend.domain.model.exception.ModelMetadataNotFoundException;
import io.so1s.backend.domain.model.exception.ModelNotFoundException;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

  private final DataTypeService dataTypeService;
  private final ModelRepository modelRepository;
  private final LibraryRepository libraryRepository;
  private final ModelMetadataRepository modelMetadataRepository;
  private final DeploymentRepository deploymentRepository;
  private final AwsS3Service awsS3Service;
  private final ModelMapper modelMapper;
  private final ModelMetadataMapper modelMetadataMapper;

  @Transactional(readOnly = true)
  public void validateDuplicateModelName(String name) {
    Optional<Model> result = modelRepository.findByName(name);
    if (result.isPresent()) {
      throw new DuplicatedModelNameException(
          String.format("Model Name is duplicated. (name : %s, created_at : %s)",
              result.get().getName(),
              result.get().getUpdatedOn()));
    }
  }

  @Transactional
  public Model createModel(ModelUploadRequestDto modelUploadRequestDto) {
    validateDuplicateModelName(modelUploadRequestDto.getName());
    Library library = validateLibrary(modelUploadRequestDto.getLibrary());
    return modelRepository.save(modelMapper.toEntity(modelUploadRequestDto, library));
  }

  @Transactional
  public ModelMetadata createModelMetadata(Model model,
      ModelUploadRequestDto modelUploadRequestDto, FileSaveResultForm fileSaveResultForm)
      throws DataTypeNotFoundException {

    dataTypeService.findDataTypeByName(modelUploadRequestDto.getInputDtype());
    dataTypeService.findDataTypeByName(modelUploadRequestDto.getOutputDtype());

    return modelMetadataRepository.save(
        modelMetadataMapper.toEntity(model, modelUploadRequestDto, fileSaveResultForm));
  }

  @Transactional(readOnly = true)
  public Library validateLibrary(String library) throws LibraryNotFoundException {
    Optional<Library> result = libraryRepository.findByName(library);
    if (result.isEmpty()) {
      throw new LibraryNotFoundException("Invalid Library.");
    }

    return result.get();
  }

  @Transactional(readOnly = true)
  public Model findModelByName(String name) throws ModelNotFoundException {

    Optional<Model> model = modelRepository.findByName(name);
    if (model.isEmpty()) {
      throw new ModelNotFoundException("Model not found.");
    }

    return model.get();
  }


  @Override
  public ModelMetadata validateExistModelMetadata(Long id) throws ModelMetadataNotFoundException {
    Optional<ModelMetadata> modelMetadata = modelMetadataRepository.findById(id);
    if (modelMetadata.isEmpty()) {
      throw new ModelMetadataNotFoundException("Invalid Model Version.");
    }

    return modelMetadata.get();
  }

  @Transactional(readOnly = true)
  @Override
  public List<ModelFindResponseDto> findModels() {
    List<Model> models = modelRepository.findAll();
    List<ModelFindResponseDto> res = new ArrayList<>();

    models.forEach(model -> modelMetadataRepository
        .findFirstByModelIdOrderByIdDesc(model.getId())
        .ifPresent(modelMetadata -> res.add(modelMapper.toFindResponseDto(model, modelMetadata))));

    return res;
  }

  @Transactional(readOnly = true)
  @Override
  public List<ModelMetadataFindResponseDto> findModelMetadatasByModelId(Long id) {
    return modelMetadataRepository.findByModelId(id)
        .stream()
        .map(modelMetadataMapper::toFindResponseDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Override
  public ModelDetailResponseDto findModelDetail(Long modelId, String version)
      throws ModelNotFoundException, ModelMetadataNotFoundException {

    Model model = modelRepository.findById(modelId)
        .orElseThrow(() -> new ModelNotFoundException("Model not found."));

    ModelMetadata modelMetadata = modelMetadataRepository.findByModelIdAndVersion(
            modelId, version)
        .orElseThrow(() -> new ModelMetadataNotFoundException("Invalid model version."));

    return modelMapper.toDetailResponseDto(model, modelMetadata);
  }

  @Override
  public ModelDeleteResponseDto deleteModel(Long modelId)
      throws ModelNotFoundException, ModelMetadataExistsException {
    Model model = modelRepository.findById(modelId)
        .orElseThrow(() -> new ModelNotFoundException("Cannot delete Model. Model not found."));

    List<ModelMetadata> modelMetadatas = modelMetadataRepository.findByModelId(modelId);

    if (!modelMetadatas.isEmpty()) {
      throw new ModelMetadataExistsException(
          "Cannot delete Model. Model Metadata exists that uses the Model.");
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
        .orElseThrow(() -> new ModelMetadataNotFoundException(
            "Cannot delete Model Metadata. Model Metadata not found."));

    Optional<Deployment> deployment = deploymentRepository.findByModelMetadata(modelMetadata);

    if (deployment.isPresent()) {
      throw new DeploymentExistsException(
          "Cannot delete Model Metadata. Deployment exists that uses the Model Metadata.");
    }

    awsS3Service.deleteFile(modelMetadata.getUrl());

    modelMetadataRepository.delete(modelMetadata);

    return ModelMetadataDeleteResponseDto.builder()
        .success(true)
        .message("모델 메타데이터가 삭제되었습니다.")
        .build();
  }
}
