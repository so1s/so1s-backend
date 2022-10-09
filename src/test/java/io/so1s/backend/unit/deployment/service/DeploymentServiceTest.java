package io.so1s.backend.unit.deployment.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import io.fabric8.istio.client.IstioClient;
import io.fabric8.istio.mock.EnableIstioMockClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.so1s.backend.domain.aws.config.S3Config;
import io.so1s.backend.domain.aws.service.AwsS3Service;
import io.so1s.backend.domain.deployment.dto.mapper.DeploymentMapper;
import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.request.ResourceRequestDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentDeleteResponseDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentFindResponseDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.deployment.exception.DeploymentStrategyNotFoundException;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.deployment.repository.DeploymentStrategyRepository;
import io.so1s.backend.domain.deployment.service.DeploymentService;
import io.so1s.backend.domain.deployment.service.DeploymentServiceImpl;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.kubernetes.service.KubernetesServiceImpl;
import io.so1s.backend.domain.kubernetes.utils.JobStatusChecker;
import io.so1s.backend.domain.library.entity.Library;
import io.so1s.backend.domain.library.repository.LibraryRepository;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.exception.ModelMetadataNotFoundException;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.domain.model.service.ModelService;
import io.so1s.backend.domain.model.service.ModelServiceImpl;
import io.so1s.backend.domain.resource.dto.mapper.ResourceMapper;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.repository.ResourceRepository;
import io.so1s.backend.domain.resource.service.ResourceService;
import io.so1s.backend.domain.resource.service.ResourceServiceImpl;
import io.so1s.backend.domain.test.entity.ABTest;
import io.so1s.backend.domain.test.exception.ABTestExistsException;
import io.so1s.backend.domain.test.repository.ABTestRepository;
import io.so1s.backend.global.config.JpaConfig;
import io.so1s.backend.global.utils.HashGenerator;
import io.so1s.backend.global.vo.Status;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DataJpaTest
@Import(JpaConfig.class)
@EnableKubernetesMockClient(crud = true)
@EnableIstioMockClient(crud = true)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = {"test"})
public class DeploymentServiceTest {

  KubernetesClient client;
  IstioClient istioClient;

  KubernetesService kubernetesService;
  DeploymentService deploymentService;
  ModelService modelService;

  @Autowired
  ModelRepository modelRepository;
  @Autowired
  LibraryRepository libraryRepository;
  @Autowired
  ModelMetadataRepository modelMetadataRepository;
  @Autowired
  DeploymentRepository deploymentRepository;
  @Autowired
  DeploymentStrategyRepository deploymentStrategyRepository;
  @Autowired
  ResourceRepository resourceRepository;
  @Autowired
  ABTestRepository abTestRepository;
  DeploymentMapper deploymentMapper = new DeploymentMapper();
  ResourceMapper resourceMapper = new ResourceMapper();
  ResourceService resourceService;
  @MockBean
  JobStatusChecker jobStatusChecker;
  @MockBean
  S3Config s3Config;
  @MockBean
  AwsS3Service awsS3UploadService;

  ResourceRequestDto resourceRequestDto;

  @BeforeEach
  void setup() {
    kubernetesService = new KubernetesServiceImpl(client, istioClient, jobStatusChecker);
    modelService = new ModelServiceImpl(modelRepository, libraryRepository,
        modelMetadataRepository, deploymentRepository, awsS3UploadService);
    resourceService = new ResourceServiceImpl(resourceRepository, resourceMapper);
    deploymentService = new DeploymentServiceImpl(deploymentRepository,
        deploymentStrategyRepository, resourceRepository, abTestRepository, kubernetesService,
        modelService, resourceService, deploymentMapper);
    resourceRequestDto = ResourceRequestDto.builder()
        .cpu("1")
        .memory("1Gi")
        .gpu("0")
        .cpuLimit("2")
        .memoryLimit("2Gi")
        .gpuLimit("0")
        .build();
  }

  @Test
  @DisplayName("새로운 Resource를 생성한다.")
  public void createResourceTest() throws Exception {
    // given
    // setup()

    // when
    Resource resource = resourceService.createResource(resourceRequestDto);
    Resource result = resourceRepository.findById(resource.getId()).get();

    // then
    assertThat(result.getCpu()).isEqualTo(resourceRequestDto.getCpu());
    assertThat(result.getMemory()).isEqualTo(resourceRequestDto.getMemory());
    assertThat(result.getGpu()).isEqualTo(resourceRequestDto.getGpu());
    assertThat(result.getCpuLimit()).isEqualTo(resourceRequestDto.getCpuLimit());
    assertThat(result.getMemoryLimit()).isEqualTo(resourceRequestDto.getMemoryLimit());
    assertThat(result.getGpuLimit()).isEqualTo(resourceRequestDto.getGpuLimit());
  }

  @Test
  @DisplayName("선택한 모델 데이터를 통해 배포를 진행한다.")
  public void createDeploymentTest() throws Exception {
    // given
    ModelMetadata modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
        .status(Status.SUCCEEDED)
        .version(HashGenerator.sha256())
        .fileName("testFile")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build());
    Resource resource = resourceRequestDto.toEntity();
    DeploymentRequestDto deploymentRequestDto = DeploymentRequestDto.builder()
        .name("testDeployment")
        .modelMetadataId(modelMetadata.getId())
        .strategy("rolling")
        .resources(resourceRequestDto)
        .build();

    // when
    Deployment deployment = deploymentService.createDeployment(resource, deploymentRequestDto);
    Optional<Deployment> result = deploymentRepository.findById(deployment.getId());

    // then
    assertThat(result.get().getName()).isEqualTo(deploymentRequestDto.getName());
    assertThat(result.get().getDeploymentStrategy().getName()).isEqualTo(
        deploymentRequestDto.getStrategy());
    assertThat(result.get().getResource().getCpu()).isEqualTo(resource.getCpu());
    assertThat(result.get().getResource().getMemory()).isEqualTo(resource.getMemory());

  }

  @Test
  @DisplayName("잘못된 모델메타데이터를 선택했을경우 ModelMetadataNotFoundException이 발생한다.")
  public void createDeploymentWrongModelMetadataTest() throws Exception {
    // given
    Resource resource = resourceRequestDto.toEntity();
    DeploymentRequestDto deploymentRequestDto = DeploymentRequestDto.builder()
        .name("testDeployment")
        .modelMetadataId(-(1L))
        .strategy("rolling")
        .resources(resourceRequestDto)
        .build();

    // when
    // then
    assertThrows(ModelMetadataNotFoundException.class, () -> {
      deploymentService.createDeployment(resource, deploymentRequestDto);
    });
  }

  @Test
  @DisplayName("잘못된 전략을 선택했을경우 DeploymentStrategyNotFoundException이 발생한다.")
  public void createDeploymentWrongStragegyTest() throws Exception {
    // given
    ModelMetadata modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
        .status(Status.SUCCEEDED)
        .version(HashGenerator.sha256())
        .fileName("testFile")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build());
    Resource resource = resourceRequestDto.toEntity();
    DeploymentRequestDto deploymentRequestDto = DeploymentRequestDto.builder()
        .name("testDeployment")
        .modelMetadataId(modelMetadata.getId())
        .strategy("not-exist-strategy")
        .resources(resourceRequestDto)
        .build();

    // when
    // then
    assertThrows(DeploymentStrategyNotFoundException.class, () -> {
      deploymentService.createDeployment(resource, deploymentRequestDto);
    });
  }

  @Test
  @DisplayName("디플로이먼트 업데이트를 한다.")
  public void updateDeployment() throws Exception {
    // given
    ModelMetadata modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
        .status(Status.SUCCEEDED)
        .version(HashGenerator.sha256())
        .fileName("firstFile")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build());
    Resource resource = resourceService.createResource(resourceRequestDto);
    DeploymentRequestDto deploymentRequestDto = DeploymentRequestDto.builder()
        .name("testDeployment")
        .modelMetadataId(modelMetadata.getId())
        .strategy("rolling")
        .resources(resourceRequestDto)
        .build();
    Deployment deployment = deploymentService.createDeployment(resource, deploymentRequestDto);

    ModelMetadata modelMetadata2 = modelMetadataRepository.save(ModelMetadata.builder()
        .status(Status.SUCCEEDED)
        .version(HashGenerator.sha256())
        .fileName("secondFile")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build());
    DeploymentRequestDto deploymentRequestDto2 = DeploymentRequestDto.builder()
        .name("testDeployment")
        .modelMetadataId(modelMetadata2.getId())
        .strategy("rolling")
        .resources(resourceRequestDto)
        .build();

    // when
    Deployment result = deploymentService.updateDeployment(deploymentRequestDto2);

    // then
    assertThat(result.getName()).isEqualTo(deployment.getName());
    assertThat(result.getModelMetadata().getFileName()).isEqualTo(modelMetadata2.getFileName());
    assertThat(result.getModelMetadata().getId()).isEqualTo(modelMetadata2.getId());
  }

  @Test
  @DisplayName("존재하지않는 디플로이먼트를 선택하여 업데이트한 경우 DeploymentNotFoundException이 발생한다.")
  public void updateDeploymentWrongNameTest() throws Exception {
    // given
    ModelMetadata modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
        .status(Status.SUCCEEDED)
        .version(HashGenerator.sha256())
        .fileName("testFile")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build());
    Resource resource = resourceRequestDto.toEntity();
    DeploymentRequestDto deploymentRequestDto = DeploymentRequestDto.builder()
        .name("not-exist-deployment")
        .modelMetadataId(modelMetadata.getId())
        .strategy("rolling")
        .resources(resourceRequestDto)
        .build();

    // when
    // then
    assertThrows(DeploymentNotFoundException.class, () -> {
      deploymentService.updateDeployment(deploymentRequestDto);
    });
  }

  @Test
  @DisplayName("디플로이먼트들을 조회한다.")
  public void findDeploymentsTest() throws Exception {
    // given
    Optional<Library> result = libraryRepository.findByName("tensorflow");
    Model model = modelRepository.save(Model.builder()
        .name("testModel")
        .library(result.get())
        .build());
    ModelMetadata modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
        .status(Status.SUCCEEDED)
        .version(HashGenerator.sha256())
        .fileName("testFile")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .model(model)
        .build());
    Resource resource = resourceService.createResource(resourceRequestDto);
    DeploymentRequestDto deploymentRequestDto = DeploymentRequestDto.builder()
        .name("testDeployment")
        .modelMetadataId(modelMetadata.getId())
        .strategy("rolling")
        .resources(resourceRequestDto)
        .build();
    Deployment deployment = deploymentService.createDeployment(resource, deploymentRequestDto);

    // when
    List<DeploymentFindResponseDto> findDeployments = deploymentService.findDeployments();
    DeploymentFindResponseDto responseDto = null;
    for (DeploymentFindResponseDto d : findDeployments) {
      if (d.getDeploymentName().equals(deployment.getName())) {
        responseDto = d;
        break;
      }
    }

    // then
    assertThat(responseDto.getDeploymentName()).isEqualTo(deployment.getName());
    assertThat(responseDto.getStatus()).isEqualTo(deployment.getStatus());
  }

  @Test
  @DisplayName("디플로이먼트를 조회한다.")
  public void findDeploymentTest() throws Exception {
    // given
    Optional<Library> result = libraryRepository.findByName("tensorflow");
    Model model = modelRepository.save(Model.builder()
        .name("testModel")
        .library(result.get())
        .build());
    ModelMetadata modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
        .status(Status.SUCCEEDED)
        .version(HashGenerator.sha256())
        .fileName("testFile")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .model(model)
        .build());
    Resource resource = resourceRequestDto.toEntity();
    DeploymentRequestDto deploymentRequestDto = DeploymentRequestDto.builder()
        .name("testDeployment")
        .modelMetadataId(modelMetadata.getId())
        .strategy("rolling")
        .resources(resourceRequestDto)
        .build();
    Deployment deployment = deploymentService.createDeployment(resource, deploymentRequestDto);

    // when
    DeploymentFindResponseDto responseDto = deploymentService.findDeployment(deployment.getId());

    // then
    assertThat(responseDto.getDeploymentName()).isEqualTo(deployment.getName());
    assertThat(responseDto.getStatus()).isEqualTo(deployment.getStatus());

  }

  @Test
  @DisplayName("디플로이먼트를 삭제한다.")
  public void deleteDeployment() throws Exception {
    // given
    Library library = libraryRepository.save(Library.builder()
        .name("testLibrary")
        .build());
    Model model = modelRepository.save(Model.builder()
        .name("testModel")
        .library(library)
        .build());
    ModelMetadata modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
        .model(model)
        .status(Status.SUCCEEDED)
        .version(HashGenerator.sha256())
        .fileName("firstFile")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build());
    Resource resource = resourceService.createResource(resourceRequestDto);
    DeploymentRequestDto deploymentRequestDto = DeploymentRequestDto.builder()
        .name("testDeployment")
        .modelMetadataId(modelMetadata.getId())
        .strategy("rolling")
        .resources(resourceRequestDto)
        .build();

    Deployment deployment = deploymentService.createDeployment(resource, deploymentRequestDto);

    kubernetesService.deployInferenceServer(
        deployment); // Controller에서 Deployment 배포가 이루어지므로 로직을 가져옴.

    // when
    DeploymentDeleteResponseDto responseDto = deploymentService.deleteDeployment(
        deployment.getId());

    // then
    assertThat(responseDto.getSuccess()).isEqualTo(true);
    assertThat(responseDto.getMessage()).isNotEmpty();

  }

  @Test
  @DisplayName("존재하지 않는 디플로이먼트를 삭제하려고 하지만, 성공하지 않는다.")
  public void deleteDeploymentButEmpty() throws Exception {
    // given
    Long deploymentId = 42L;

    // when & then
    assertThrowsExactly(DeploymentNotFoundException.class,
        () -> deploymentService.deleteDeployment(deploymentId));

  }

  @Test
  @DisplayName("AB 테스트가 있는 디플로이먼트를 삭제하려고 하지만, 성공하지 않는다.")
  public void deleteDeploymentButHasABTest() throws Exception {
    // given
    Library library = libraryRepository.save(Library.builder()
        .name("testLibrary")
        .build());
    Model model = modelRepository.save(Model.builder()
        .name("testModel")
        .library(library)
        .build());
    ModelMetadata modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
        .model(model)
        .status(Status.SUCCEEDED)
        .version(HashGenerator.sha256())
        .fileName("firstFile")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build());
    Resource resource = resourceService.createResource(resourceRequestDto);
    DeploymentRequestDto deploymentRequestDto = DeploymentRequestDto.builder()
        .name("testDeployment")
        .modelMetadataId(modelMetadata.getId())
        .strategy("rolling")
        .resources(resourceRequestDto)
        .build();

    Deployment deployment = deploymentService.createDeployment(resource, deploymentRequestDto);

    kubernetesService.deployInferenceServer(
        deployment); // Controller에서 Deployment 배포가 이루어지므로 로직을 가져옴.

    ABTest abTest = ABTest.builder()
        .name("testABTest")
        .a(deployment)
        .b(deployment)
        .domain("*.so1s.io")
        .build();

    abTestRepository.save(abTest);

    // when & then
    assertThrowsExactly(ABTestExistsException.class, () -> deploymentService.deleteDeployment(
        deployment.getId()));

  }
}
