package io.so1s.backend.unit.test.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import io.fabric8.istio.mock.EnableIstioMockClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.Resource;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.deployment.repository.ResourceRepository;
import io.so1s.backend.domain.model.entity.Library;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.domain.test.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.dto.response.ABTestDeleteResponseDto;
import io.so1s.backend.domain.test.entity.ABTest;
import io.so1s.backend.domain.test.repository.ABTestRepository;
import io.so1s.backend.domain.test.service.ABTestService;
import io.so1s.backend.global.entity.Status;
import io.so1s.backend.global.error.exception.ABTestNotFoundException;
import io.so1s.backend.global.error.exception.DeploymentNotFoundException;
import io.so1s.backend.global.utils.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@EnableKubernetesMockClient(crud = true)
@EnableIstioMockClient(crud = true)
@ExtendWith(MockitoExtension.class)
@WithMockUser
@SpringBootTest
@ActiveProfiles(profiles = {"test"})
public class ABTestServiceTest {

  @Autowired
  ABTestRepository abTestRepository;
  @Autowired
  DeploymentRepository deploymentRepository;
  @Autowired
  ModelRepository modelRepository;
  @Autowired
  ModelMetadataRepository modelMetadataRepository;
  @Autowired
  ResourceRepository resourceRepository;
  @Autowired
  ABTestService abTestService;

  Model model;
  ModelMetadata modelMetadata;

  Resource resource;

  Deployment a;

  Deployment b;

  ABTestRequestDto baseRequestDto = ABTestRequestDto.builder()
      .a(42L)
      .b(43L)
      .domain("so1s.io")
      .name("abTest")
      .build();

  @BeforeEach
  public void setUp() {
    model = modelRepository.save(Model.builder()
        .name("testModel")
        .library(Library.builder()
            .name("torch")
            .build())
        .build());

    modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
        .status(Status.SUCCEEDED)
        .version(HashGenerator.sha256())
        .fileName("titanic.h5")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .model(model)
        .build());

    resource = resourceRepository.save(Resource.builder()
        .cpu("1")
        .memory("1Gi")
        .gpu("0")
        .cpuLimit("2")
        .memoryLimit("2Gi")
        .gpuLimit("0")
        .build());

    a = deploymentRepository.save(Deployment.builder()
        .name("aDeployment")
        .status(Status.PENDING)
        .modelMetadata(modelMetadata)
        .endPoint("a.so1s.io")
        .resource(resource)
        .build());

    b = deploymentRepository.save(Deployment.builder()
        .name("bDeployment")
        .status(Status.PENDING)
        .modelMetadata(modelMetadata)
        .endPoint("b.so1s.io")
        .resource(resource)
        .build());
  }

  @Test
  @DisplayName("Deployment 없이 AB Test를 생성하면 오류가 발생한다.")
  public void createABTestWithoutDeployment() throws Exception {
    // given
    ABTestRequestDto abTestRequestDto = baseRequestDto;

    // when & then
    assertThrowsExactly(DeploymentNotFoundException.class,
        () -> abTestService.createABTest(abTestRequestDto));

  }

  @Test
  @DisplayName("AB Test를 생성한다.")
  public void createABTest() throws Exception {
    // given

    ABTestRequestDto abTestRequestDto = ABTestRequestDto.builder()
        .a(a.getId())
        .b(b.getId())
        .name(baseRequestDto.getName())
        .domain(baseRequestDto.getDomain()).build();

    ABTest abTest = abTestService.createABTest(abTestRequestDto);

    assertThat(abTest).isNotNull();
    assertThat(abTest.getA().getId()).isEqualTo(abTestRequestDto.getA());
    assertThat(abTest.getB().getId()).isEqualTo(abTestRequestDto.getB());
    assertThat(abTest.getName()).isEqualTo(abTestRequestDto.getName());
    assertThat(abTest.getDomain()).isEqualTo(abTestRequestDto.getDomain());
  }

  @Test
  @DisplayName("잘못된 ID로 AB Test를 삭제하면 오류가 발생한다.")
  public void deleteABTestWithWrongId() throws Exception {
    // given

    // when & then
    assertThrowsExactly(ABTestNotFoundException.class, () -> abTestService.deleteABTest(42L));
  }

  @Test
  @DisplayName("AB Test를 삭제한다.")
  public void deleteABTest() throws Exception {
    // given
    ABTest abTest = abTestRepository.save(ABTest.builder()
        .a(a)
        .b(b)
        .name(baseRequestDto.getName())
        .domain(baseRequestDto.getDomain())
        .build());

    // when
    ABTestDeleteResponseDto deleteResponseDto = abTestService.deleteABTest(abTest.getId());

    // then
    assertThat(deleteResponseDto).isNotNull();
    assertThat(deleteResponseDto.getSuccess()).isTrue();
    assertThat(deleteResponseDto.getMessage()).isNotEmpty();
  }
}
