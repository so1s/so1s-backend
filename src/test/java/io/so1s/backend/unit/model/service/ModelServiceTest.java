package io.so1s.backend.unit.model.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import io.so1s.backend.domain.model.service.ModelServiceImpl;
import io.so1s.backend.global.config.JpaConfig;
import io.so1s.backend.global.error.exception.DuplicateModelNameException;
import io.so1s.backend.global.error.exception.LibraryNotFoundException;
import io.so1s.backend.global.error.exception.ModelMetadataNotFoundException;
import io.so1s.backend.global.error.exception.ModelNotFoundException;
import io.so1s.backend.global.utils.HashGenerator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DataJpaTest
@Import(JpaConfig.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = {"test"})
class ModelServiceTest {

  @InjectMocks
  ModelServiceImpl modelService;
  @Autowired
  ModelRepository modelRepository;
  @Autowired
  LibraryRepository libraryRepository;
  @Autowired
  ModelMetadataRepository modelMetadataRepository;

  String name = "testModel";
  String library = "tensorflow";
  String version;
  ModelUploadRequestDto modelUploadRequestDto;

  @BeforeEach
  public void setup() {
    version = HashGenerator.sha256();
    modelService = new ModelServiceImpl(modelRepository, libraryRepository,
        modelMetadataRepository);

    modelUploadRequestDto = ModelUploadRequestDto.builder()
        .name(name)
        .library(library)
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build();
  }


  @Test
  @Transactional
  @DisplayName("모델을 저장한다.")
  public void createModelTest() throws Exception {
    // given
    // setup()

    // when
    modelService.createModel(modelUploadRequestDto);
    Model result = modelRepository.findByName(modelUploadRequestDto.getName()).get();

    // then
    assertThat(result.getName()).isEqualTo(modelUploadRequestDto.getName());
    assertThat(result.getLibrary().getName()).isEqualTo(modelUploadRequestDto.getLibrary());
  }

  @Test
  @Transactional
  @DisplayName("모델 이름이 중복되면 DuplicateModelNameException이 발생한다.")
  public void createModelDuplicateNameTest() throws Exception {
    // given
    // setup()
    ModelUploadRequestDto modelUploadRequestDto2 = ModelUploadRequestDto.builder()
        .name(name)
        .library(library)
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build();

    // when
    modelService.createModel(modelUploadRequestDto);

    // then
    assertThrows(DuplicateModelNameException.class, () -> {
      modelService.createModel(modelUploadRequestDto2);
    });
  }

  @Test
  @Transactional
  @DisplayName("모델 메타데이터를 저장한다.")
  public void createModelMetadataTest() throws Exception {
    // given
    // setup()
    FileSaveResultForm saveResult = FileSaveResultForm.builder()
        .savedName("fileName")
        .url("http://test.com/")
        .build();

    // when
    Model model = Model.builder()
        .name("testModel")
        .library(Library.builder()
            .name("testLibrary")
            .build())
        .build();
    ModelMetadata modelMetadata = modelService.createModelMetadata(
        model, modelUploadRequestDto,
        saveResult);
    ModelMetadata result = modelMetadataRepository.findById(modelMetadata.getId()).get();

    // then
    assertThat(result.getModel().getName()).isEqualTo(modelUploadRequestDto.getName());
    assertThat(result.getUrl()).isEqualTo(saveResult.getUrl());
    assertThat(result.getFileName()).isEqualTo(saveResult.getSavedName());
  }

  @Test
  @Transactional
  @DisplayName("잘못된 라이브러리를 설정하면 LibraryNotFoundException이 발생한다.")
  public void createModelWrongLibraryTest() throws Exception {
    // given
    // setup()
    ModelUploadRequestDto modelUploadRequestDto2 = ModelUploadRequestDto.builder()
        .name("testModel2")
        .library("tonsorflow")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build();

    // when
    modelService.createModel(modelUploadRequestDto);

    // then
    assertThrows(LibraryNotFoundException.class, () -> {
      modelService.createModel(modelUploadRequestDto2);
    });
  }

  @Test
  @Transactional
  @DisplayName("모델 조회 서비스를 테스트한다.")
  public void findModelsTest() throws Exception {
    // given
    FileSaveResultForm saveResult = FileSaveResultForm.builder()
        .savedName("fileName")
        .url("http://test.com/")
        .build();
    Model model = modelService.createModel(modelUploadRequestDto);
    ModelMetadata modelMetadata = modelService.createModelMetadata(
        model, modelUploadRequestDto, saveResult);

    // when
    List<ModelFindResponseDto> findModels = modelService.findModels();
    ModelFindResponseDto responseDto = null;
    for (ModelFindResponseDto findModel : findModels) {
      if (findModel.getName().equals(model.getName())) {
        responseDto = findModel;
        break;
      }
    }

    // then
    assertThat(responseDto.getName()).isEqualTo(model.getName());
    assertThat(responseDto.getStatus()).isEqualTo(modelMetadata.getStatus());
    assertThat(responseDto.getVersion()).isEqualTo(modelMetadata.getVersion());
    assertThat(responseDto.getLibrary()).isEqualTo(model.getLibrary().getName());
  }

  @Test
  @Transactional
  @DisplayName("특정 모델 버전 조회 서비스를 테스트한다.")
  public void findModelMetadatasByModelIdTest() throws Exception {
    // given
    FileSaveResultForm saveResult = FileSaveResultForm.builder()
        .savedName("fileName")
        .url("http://test.com/")
        .build();
    Model model = modelService.createModel(modelUploadRequestDto);
    ModelMetadata modelMetadata = modelService.createModelMetadata(
        model, modelUploadRequestDto, saveResult);
    ModelMetadata modelMetadata2 = modelService.createModelMetadata(
        model, modelUploadRequestDto, saveResult);

    // when
    List<ModelMetadataFindResponseDto> find = modelService.findModelMetadatasByModelId(
        model.getId());

    // then
    assertThat(find.get(0).getAge()).isEqualTo(modelMetadata.getUpdatedOn());
    assertThat(find.get(0).getVersion()).isEqualTo(modelMetadata.getVersion());
    assertThat(find.get(0).getStatus()).isEqualTo(modelMetadata.getStatus());
    assertThat(find.get(0).getUrl()).isEqualTo(modelMetadata.getUrl());

    assertThat(find.get(1).getAge()).isEqualTo(modelMetadata2.getUpdatedOn());
    assertThat(find.get(1).getVersion()).isEqualTo(modelMetadata2.getVersion());
    assertThat(find.get(1).getStatus()).isEqualTo(modelMetadata2.getStatus());
    assertThat(find.get(1).getUrl()).isEqualTo(modelMetadata2.getUrl());
  }

  @Test
  @Transactional
  @DisplayName("특정 모델 특정 버전 조회 서비스를 테스트한다.")
  public void findModelDetailTest() throws Exception {
    // given
    FileSaveResultForm saveResult = FileSaveResultForm.builder()
        .savedName("fileName")
        .url("http://test.com/")
        .build();
    Model model = modelService.createModel(modelUploadRequestDto);
    ModelMetadata modelMetadata = modelService.createModelMetadata(
        model, modelUploadRequestDto, saveResult);

    // when
    ModelDetailResponseDto find = modelService.findModelDetail(model.getId(),
        modelMetadata.getVersion());

    // then
    assertThat(find.getAge()).isEqualTo(modelMetadata.getUpdatedOn());
    assertThat(find.getName()).isEqualTo(model.getName());
    assertThat(find.getVersion()).isEqualTo(modelMetadata.getVersion());
    assertThat(find.getStatus()).isEqualTo(modelMetadata.getStatus());
    assertThat(find.getUrl()).isEqualTo(modelMetadata.getUrl());
    assertThat(find.getLibrary()).isEqualTo(model.getLibrary().getName());
    assertThat(find.getInputShape()).isEqualTo(modelMetadata.getInputShape());
    assertThat(find.getInputDtype()).isEqualTo(modelMetadata.getInputDtype());
    assertThat(find.getOutputShape()).isEqualTo(modelMetadata.getOutputShape());
    assertThat(find.getOutputDtype()).isEqualTo(modelMetadata.getOutputDtype());
  }

  @Test
  @Transactional
  @DisplayName("잘못된 모델 id를 기반으로 조회했을때 ModelNotFoundException을 발생시킨다.")
  public void findModelDetailWrongModelIdTest() throws Exception {
    // given
    FileSaveResultForm saveResult = FileSaveResultForm.builder()
        .savedName("fileName")
        .url("http://test.com/")
        .build();
    Model model = modelService.createModel(modelUploadRequestDto);
    ModelMetadata modelMetadata = modelService.createModelMetadata(
        model, modelUploadRequestDto, saveResult);

    // when
    // then
    assertThrows(ModelNotFoundException.class, () -> modelService.findModelDetail(model.getId() + 1,
        modelMetadata.getVersion()));
  }

  @Test
  @Transactional
  @DisplayName("잘못된 모델 version을 기반으로 조회했을때 ModelMetadataNotFoundException을 발생시킨다.")
  public void findModelDetailWrongVersionTest() throws Exception {
    // given
    FileSaveResultForm saveResult = FileSaveResultForm.builder()
        .savedName("fileName")
        .url("http://test.com/")
        .build();
    Model model = modelService.createModel(modelUploadRequestDto);
    ModelMetadata modelMetadata = modelService.createModelMetadata(
        model, modelUploadRequestDto, saveResult);

    // when
    // then
    assertThrows(ModelMetadataNotFoundException.class,
        () -> modelService.findModelDetail(model.getId(),
            modelMetadata.getVersion() + "-not-exist"));
  }
}