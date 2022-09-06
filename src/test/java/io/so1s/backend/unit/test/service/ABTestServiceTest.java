package io.so1s.backend.unit.test.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import io.fabric8.istio.mock.EnableIstioMockClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.Resource;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.deployment.repository.ResourceRepository;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.entity.Library;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.LibraryRepository;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.domain.test.dto.request.ABTestRequestDto;
import io.so1s.backend.domain.test.dto.response.ABTestDeleteResponseDto;
import io.so1s.backend.domain.test.dto.service.ABTestCreateDto;
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
import org.springframework.boot.test.mock.mockito.MockBean;
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
  @Autowired
  LibraryRepository libraryRepository;
  @MockBean
  KubernetesService kubernetesService;

  Library library;
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

  ABTest baseABTestEntity;

  @BeforeEach
  public void setUp() {
    library = libraryRepository.save(Library.builder().name("torch").build());

    model = modelRepository.save(Model.builder()
        .name("testModel")
        .library(library)
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

    baseABTestEntity = ABTest.builder()
        .a(a)
        .b(b)
        .name(baseRequestDto.getName())
        .domain(baseRequestDto.getDomain())
        .build();
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
  @DisplayName("AB Test를 생성한 뒤 삭제한다.")
  public void createABTest() throws Exception {
    // given

    ABTestRequestDto abTestRequestDto = ABTestRequestDto.builder()
        .a(a.getId())
        .b(b.getId())
        .name(baseRequestDto.getName())
        .domain(baseRequestDto.getDomain()).build();

    given(kubernetesService.deployABTest(any())).willReturn(true);

    // when
    ABTestCreateDto createDto = abTestService.createABTest(abTestRequestDto);

    // then
    assertThat(createDto).isNotNull();

    ABTest abTest = createDto.getEntity();
    boolean success = createDto.getSuccess();

    assertThat(success).isTrue();
    assertThat(abTest).isNotNull();
    assertThat(abTest.getA().getId()).isEqualTo(abTestRequestDto.getA());
    assertThat(abTest.getB().getId()).isEqualTo(abTestRequestDto.getB());
    assertThat(abTest.getName()).isEqualTo(abTestRequestDto.getName());
    assertThat(abTest.getDomain()).isEqualTo(abTestRequestDto.getDomain());

    // Clean up

    // given

    given(kubernetesService.deleteABTest(any())).willReturn(true);

    // when
    ABTestDeleteResponseDto deleteResponseDto = abTestService.deleteABTest(abTest.getId());

    // then
    assertThat(deleteResponseDto).isNotNull();
    assertThat(deleteResponseDto.getSuccess()).isTrue();
    assertThat(deleteResponseDto.getMessage()).isNotEmpty();
  }

  @Test
  @DisplayName("AB Test를 생성한 뒤 삭제하려고 하지만, KubernetesClientException 문제로 두 케이스 모두 실패 처리된다.")
  public void createABTestFailed() throws Exception {
    // given
    ABTestRequestDto abTestRequestDto = ABTestRequestDto.builder()
        .a(a.getId())
        .b(b.getId())
        .name(baseRequestDto.getName())
        .domain(baseRequestDto.getDomain())
        .build();

    given(kubernetesService.deployABTest(any())).willReturn(false);

    // when
    ABTestCreateDto createDto = abTestService.createABTest(abTestRequestDto);

    // then
    assertThat(createDto).isNotNull();

    ABTest abTest = createDto.getEntity();
    boolean success = createDto.getSuccess();

    assertThat(success).isFalse();
    assertThat(abTest).isNotNull();
    assertThat(abTest.getA().getId()).isEqualTo(abTestRequestDto.getA());
    assertThat(abTest.getB().getId()).isEqualTo(abTestRequestDto.getB());
    assertThat(abTest.getName()).isEqualTo(abTestRequestDto.getName());
    assertThat(abTest.getDomain()).isEqualTo(abTestRequestDto.getDomain());

    // Clean up

    // given
    given(kubernetesService.deleteABTest(any())).willReturn(false);

    // when
    ABTestDeleteResponseDto deleteResponseDto = abTestService.deleteABTest(abTest.getId());

    // then
    assertThat(deleteResponseDto).isNotNull();
    assertThat(deleteResponseDto.getSuccess()).isFalse();
    assertThat(deleteResponseDto.getMessage()).isNotEmpty();
  }

  @Test
  @DisplayName("잘못된 ID로 AB Test를 삭제하면 오류가 발생한다.")
  public void deleteABTestWithWrongId() throws Exception {
    // given

    // when & then
    assertThrowsExactly(ABTestNotFoundException.class, () -> abTestService.deleteABTest(42L));
  }

  @Test
  @DisplayName("잘못된 Name으로 AB Test를 업데이트하면 오류가 발생한다.")
  public void updateABTestWithWrongName() throws Exception {
    // given
    ABTestRequestDto abTestRequestDto = ABTestRequestDto.builder()
        .a(a.getId())
        .b(b.getId())
        .name("wrong")
        .domain(baseRequestDto.getDomain())
        .build();

    // when & then
    assertThrowsExactly(ABTestNotFoundException.class,
        () -> abTestService.updateABTest(abTestRequestDto));
  }

  @Test
  @DisplayName("잘못된 A Id로 AB Test를 업데이트하면 오류가 발생한다.")
  public void updateABTestWithWrongA() throws Exception {
    // given
    abTestRepository.save(baseABTestEntity);

    ABTestRequestDto abTestRequestDto = ABTestRequestDto.builder()
        .a(42L)
        .b(b.getId())
        .name(baseRequestDto.getName())
        .domain(baseRequestDto.getDomain())
        .build();

    // when & then
    assertThrowsExactly(DeploymentNotFoundException.class,
        () -> abTestService.updateABTest(abTestRequestDto));
  }

  @Test
  @DisplayName("잘못된 B Id로 AB Test를 업데이트하면 오류가 발생한다.")
  public void updateABTestWithWrongB() throws Exception {
    // given
    abTestRepository.save(baseABTestEntity);

    ABTestRequestDto abTestRequestDto = ABTestRequestDto.builder()
        .a(a.getId())
        .b(43L)
        .name(baseRequestDto.getName())
        .domain(baseRequestDto.getDomain()).build();

    // when & then
    assertThrowsExactly(DeploymentNotFoundException.class,
        () -> abTestService.updateABTest(abTestRequestDto));
  }

  @Test
  @DisplayName("AB Test를 업데이트한다.")
  public void updateABTest() throws Exception {
    // given
    abTestRepository.save(baseABTestEntity);

    ABTestRequestDto abTestRequestDto = ABTestRequestDto.builder()
        .a(a.getId())
        .b(b.getId())
        .name(baseRequestDto.getName())
        .domain("updated.so1s.io")
        .build();

    // when
    ABTest updated = abTestService.updateABTest(abTestRequestDto);

    // then
    assertThat(updated).isNotNull();
    assertThat(updated.getA().getId()).isEqualTo(a.getId());
    assertThat(updated.getB().getId()).isEqualTo(b.getId());
    assertThat(updated.getName()).isEqualTo(abTestRequestDto.getName());
    assertThat(updated.getDomain()).isEqualTo("updated.so1s.io");
  }
}
