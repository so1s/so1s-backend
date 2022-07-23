package io.so1s.backend.unit.model.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.so1s.backend.domain.aws.service.FileSaveResultForm;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.domain.model.service.ModelServiceImpl;
import io.so1s.backend.global.error.exception.DuplicateModelNameException;
import io.so1s.backend.global.utils.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = {"test"})
class ModelServiceTest {

  @InjectMocks
  ModelServiceImpl modelService;
  @Autowired
  ModelRepository modelRepository;
  @Autowired
  ModelMetadataRepository modelMetadataRepository;

  String name = "testModel";
  String library = "tensorflow";
  String version;
  ModelUploadRequestDto modelUploadRequestDto;

  @BeforeEach
  public void setup() {
    version = HashGenerator.sha256();
    modelService = new ModelServiceImpl(modelRepository, modelMetadataRepository);

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
    assertThat(result.getLibrary()).isEqualTo(modelUploadRequestDto.getLibrary());
  }

  @Test
  @Transactional
  @DisplayName("모델 이름이 중복되면 DuplicateModelNameException이 발생한다.")
  public void createModelExceptionTest() throws Exception {
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
        .fileName("fileName")
        .url("http://test.com/")
        .build();

    // when
    ModelMetadata modelMetadata = modelService.createModelMetadata(
        modelUploadRequestDto.toModelEntity(), modelUploadRequestDto,
        saveResult);
    ModelMetadata result = modelMetadataRepository.findById(modelMetadata.getId()).get();

    // then
    assertThat(result.getModel().getName()).isEqualTo(modelUploadRequestDto.getName());
    assertThat(result.getUrl()).isEqualTo(saveResult.getUrl());
    assertThat(result.getFileName()).isEqualTo(saveResult.getFileName());
  }
}