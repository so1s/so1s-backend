package io.so1s.backend.unit.model.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.BDDMockito.given;

import com.amazonaws.services.s3.AmazonS3;
import io.findify.s3mock.S3Mock;
import io.so1s.backend.domain.aws.config.S3Config;
import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.aws.service.AwsS3Service;
import io.so1s.backend.domain.aws.service.FileUploadService;
import io.so1s.backend.domain.deployment.dto.request.Standard;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import io.so1s.backend.domain.deployment.exception.DeploymentExistsException;
import io.so1s.backend.domain.deployment.exception.LibraryNotFoundException;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.deployment_strategy.repository.DeploymentStrategyRepository;
import io.so1s.backend.domain.library.entity.Library;
import io.so1s.backend.domain.library.repository.LibraryRepository;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelDeleteResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelDetailResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelFindResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataFindResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.exception.DuplicatedModelNameException;
import io.so1s.backend.domain.model.exception.ModelMetadataNotFoundException;
import io.so1s.backend.domain.model.exception.ModelNotFoundException;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.domain.model.service.DataTypeService;
import io.so1s.backend.domain.model.service.ModelService;
import io.so1s.backend.domain.registry.entity.Registry;
import io.so1s.backend.domain.registry.repository.RegistryRepository;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.repository.ResourceRepository;
import io.so1s.backend.global.config.RegistryDataConfig;
import io.so1s.backend.global.utils.HashGenerator;
import io.so1s.backend.global.vo.Status;
import io.so1s.backend.integration.aws.service.S3MockConfig;
import io.so1s.backend.unit.kubernetes.config.TestKubernetesConfig;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Import(S3MockConfig.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestKubernetesConfig.class, RegistryDataConfig.class})
@ActiveProfiles(profiles = {"test"})
// Flush S3Mock Server After Test
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class ModelServiceTest {

  String name = "testModel";
  String library = "tensorflow";
  String version = HashGenerator.sha256();

  ModelUploadRequestDto modelUploadRequestDto;
  @Autowired
  Registry registry;
  @Autowired
  ModelService modelService;
  @MockBean
  DataTypeService dataTypeService;
  @Autowired
  AwsS3Service awsS3UploadService;
  @Autowired
  FileUploadService fileUploadService;
  @Autowired
  RegistryRepository registryRepository;
  @Autowired
  ModelRepository modelRepository;
  @Autowired
  LibraryRepository libraryRepository;
  @Autowired
  ModelMetadataRepository modelMetadataRepository;
  @Autowired
  DeploymentRepository deploymentRepository;
  @Autowired
  ResourceRepository resourceRepository;
  @Autowired
  DeploymentStrategyRepository deploymentStrategyRepository;

  @BeforeAll
  static void setUp(@Autowired S3Config s3Config, @Autowired S3Mock s3Mock,
      @Autowired AmazonS3 amazonS3) {
    amazonS3.createBucket(s3Config.getBucket());
  }

  @BeforeEach
  void setUpData() {
    registry = registryRepository.save(registry);

    modelUploadRequestDto = ModelUploadRequestDto.builder()
        .name(name)
        .library(library)
        .registryId(registry.getId())
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .deviceType("cpu")
        .build();
  }

  @AfterAll
  static void tearDown(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
    amazonS3.shutdown();
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
        .deviceType("gpu")
        .build();

    // when
    modelService.createModel(modelUploadRequestDto);

    // then
    assertThrows(DuplicatedModelNameException.class, () -> {
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

    given(dataTypeService.findDataTypeByName(modelUploadRequestDto.getInputDtype())).willReturn(
        null);
    given(dataTypeService.findDataTypeByName(modelUploadRequestDto.getOutputDtype())).willReturn(
        null);

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
        .deviceType("cpu")
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

    given(dataTypeService.findDataTypeByName(modelUploadRequestDto.getInputDtype())).willReturn(
        null);
    given(dataTypeService.findDataTypeByName(modelUploadRequestDto.getOutputDtype())).willReturn(
        null);

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

    given(dataTypeService.findDataTypeByName(modelUploadRequestDto.getInputDtype())).willReturn(
        null);
    given(dataTypeService.findDataTypeByName(modelUploadRequestDto.getOutputDtype())).willReturn(
        null);

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

    given(dataTypeService.findDataTypeByName(modelUploadRequestDto.getInputDtype())).willReturn(
        null);
    given(dataTypeService.findDataTypeByName(modelUploadRequestDto.getOutputDtype())).willReturn(
        null);

    // when
    Model model = modelService.createModel(modelUploadRequestDto);
    ModelMetadata modelMetadata = modelService.createModelMetadata(
        model, modelUploadRequestDto, saveResult);

    // then
    assertThrows(ModelMetadataNotFoundException.class,
        () -> modelService.findModelDetail(model.getId(),
            modelMetadata.getVersion() + "-not-exist"));
  }

  @Test
  @DisplayName("Model을 삭제한다.")
  public void deleteModel() throws Exception {
    // given
    FileSaveResultForm saveResult = FileSaveResultForm.builder()
        .savedName("fileName")
        .url("http://test.com/")
        .build();
    Model model = modelService.createModel(modelUploadRequestDto);

    // when
    ModelDeleteResponseDto responseDto = modelService.deleteModel(model.getId());

    // then
    assertThat(responseDto.getSuccess()).isTrue();
    assertThat(responseDto.getMessage()).isNotEmpty();
  }

  @Test
  @DisplayName("ModelMetadata가 존재하는 상태로 Model을 삭제하면 관련된 ModelMetadata가 모두 삭제된다.")
  public void deleteModelWithModelMetadata() throws Exception {
    // given
    FileSaveResultForm saveResult = FileSaveResultForm.builder()
        .savedName("fileName")
        .url("http://test.com/")
        .build();
    Model model = modelService.createModel(modelUploadRequestDto);
    ModelMetadata modelMetadata = modelService.createModelMetadata(
        model, modelUploadRequestDto, saveResult);

    // when
    modelService.deleteModel(model.getId());

    // then
    assertThat(modelRepository.findById(model.getId())).isEmpty();
    assertThat(modelMetadataRepository.findById(modelMetadata.getId())).isEmpty();
  }

  @Test
  @DisplayName("Deployment가 존재하는 상태로 ModelMetadata를 삭제하면 DeploymentExistsException이 발생한다.")
  public void deleteModelMetadataWithDeployment() throws Exception {
    // given
    FileSaveResultForm saveResult = FileSaveResultForm.builder()
        .savedName("fileName")
        .url("http://test.com/")
        .build();
    Model model = modelService.createModel(modelUploadRequestDto);

    given(dataTypeService.findDataTypeByName(modelUploadRequestDto.getInputDtype())).willReturn(
        null);
    given(dataTypeService.findDataTypeByName(modelUploadRequestDto.getOutputDtype())).willReturn(
        null);

    ModelMetadata modelMetadata = modelService.createModelMetadata(
        model, modelUploadRequestDto, saveResult);

    Resource resource = resourceRepository.save(Resource.builder()
        .name("deleteModelMetadataWithDeployment")
        .cpu("1")
        .memory("1Gi")
        .gpu("0")
        .cpuLimit("2")
        .memoryLimit("2Gi")
        .gpuLimit("0")
        .build());

    DeploymentStrategy deploymentStrategy = deploymentStrategyRepository.save(
        DeploymentStrategy.builder().name("rolling-update").build());

    Deployment deployment = deploymentRepository.save(Deployment.builder()
        .name("test-deployment")
        .endPoint("www.test.io")
        .status(Status.PENDING)
        .standard(Standard.LATENCY)
        .standardValue(20)
        .maxReplicas(10)
        .minReplicas(1)
        .modelMetadata(modelMetadata)
        .deploymentStrategy(deploymentStrategy)
        .resource(resource)
        .build());

    // when & then
    assertThrowsExactly(DeploymentExistsException.class, () -> modelService.deleteModelMetadata(
        model.getId(),
        modelMetadata.getVersion()));
  }

  @Test
  @Transactional
  @DisplayName("잘못된 이름의 모델을 저장하면 IllegalArgumentException이 발생합니다.")
  public void createWrongNameModelTest() throws Exception {
    // given
    ModelUploadRequestDto wrongModelUploadRequestDto = ModelUploadRequestDto.builder()
        .name("modelName!!")
        .library(library)
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .deviceType("cpu")
        .build();

    // when & then
    assertThrowsExactly(IllegalArgumentException.class,
        () -> modelService.createModel(wrongModelUploadRequestDto));
  }
}