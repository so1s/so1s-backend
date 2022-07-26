package io.so1s.backend.unit.deployment.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.request.ResourceRequestDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import io.so1s.backend.domain.deployment.entity.Resource;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.deployment.repository.DeploymentStrategyRepository;
import io.so1s.backend.domain.deployment.repository.ResourceRepository;
import io.so1s.backend.domain.deployment.service.DeploymentServiceImpl;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.global.utils.HashGenerator;
import java.util.Optional;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableKubernetesMockClient(crud = true)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = {"test"})
public class DeploymentServiceTest {

  KubernetesClient client;

  KubernetesService kubernetesService;
  DeploymentServiceImpl deploymentService;
  @Autowired
  ModelMetadataRepository modelMetadataRepository;
  @Autowired
  DeploymentRepository deploymentRepository;
  @Autowired
  DeploymentStrategyRepository deploymentStrategyRepository;
  @Autowired
  ResourceRepository resourceRepository;

  ResourceRequestDto resourceRequestDto;

  @BeforeEach
  void setup() {
    kubernetesService = new KubernetesService(client);
    deploymentService = new DeploymentServiceImpl(modelMetadataRepository, deploymentRepository,
        deploymentStrategyRepository, resourceRepository);

    resourceRequestDto = ResourceRequestDto.builder()
        .cpu("1")
        .memory("1Gi")
        .gpu("0")
        .cpuLimit("2")
        .memoryLimit("2Gi")
        .gpuLimit("0")
        .build();
  }

  @After
  void deleteData() {
    modelMetadataRepository.deleteAll();
    deploymentRepository.deleteAll();
    deploymentStrategyRepository.deleteAll();
    resourceRepository.deleteAll();
  }

  @Test
  @DisplayName("새로운 Resource를 생성한다.")
  public void createResourceTest() throws Exception {
    // given
    // setup()

    // when
    Resource resource = deploymentService.createResource(resourceRequestDto);
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
        .status("success")
        .version(HashGenerator.sha256())
        .fileName("testFile")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build());
    deploymentStrategyRepository.save(DeploymentStrategy.builder()
        .name("rolling")
        .build());
    Resource resource = resourceRequestDto.toEntity();
    DeploymentRequestDto deploymentRequestDto = DeploymentRequestDto.builder()
        .name("testDeployment")
        .modelMetadataId(modelMetadata.getId())
        .modelVersion(HashGenerator.sha256())
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
  @DisplayName("잘못된 모델메타데이터를 선택했을경우 IllgalArgumentException이 발생한다.")
  public void createDeploymentWrongModelMetadataTest() throws Exception {
    // given
    Resource resource = resourceRequestDto.toEntity();
    DeploymentRequestDto deploymentRequestDto = DeploymentRequestDto.builder()
        .name("testDeployment")
        .modelMetadataId(1L)
        .modelVersion(HashGenerator.sha256())
        .strategy("rolling")
        .resources(resourceRequestDto)
        .build();

    // when
    // then
    assertThrows(IllegalArgumentException.class, () -> {
      deploymentService.createDeployment(resource, deploymentRequestDto);
    });
  }

  @Test
  @DisplayName("잘못된 전략을 선택했을경우 IllegalArgumentException 발생한다.")
  public void createDeploymentWrongStragegyTest() throws Exception {
    // given
    ModelMetadata modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
        .status("success")
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
        .modelMetadataId(1L)
        .modelVersion(HashGenerator.sha256())
        .strategy("rolling")
        .resources(resourceRequestDto)
        .build();

    // when
    // then
    assertThrows(IllegalArgumentException.class, () -> {
      deploymentService.createDeployment(resource, deploymentRequestDto);
    });
  }
}
