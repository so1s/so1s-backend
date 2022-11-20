package io.so1s.backend.unit.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.istio.mock.EnableIstioMockClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.so1s.backend.domain.deployment.dto.request.Standard;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.library.entity.Library;
import io.so1s.backend.domain.library.repository.LibraryRepository;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.repository.ResourceRepository;
import io.so1s.backend.domain.test.v2.controller.ABNTestController;
import io.so1s.backend.domain.test.v2.dto.common.ABNTestElementDto;
import io.so1s.backend.domain.test.v2.dto.request.ABNTestRequestDto;
import io.so1s.backend.domain.test.v2.service.ABNTestService;
import io.so1s.backend.domain.test.v2.service.internal.ABNTestKubernetesService;
import io.so1s.backend.global.utils.HashGenerator;
import io.so1s.backend.global.vo.Status;
import io.so1s.backend.unit.kubernetes.config.TestKubernetesConfig;
import java.util.List;
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
@SpringBootTest(classes = {TestKubernetesConfig.class})
@ActiveProfiles(profiles = {"test"})
public class ABNTestServiceTest {

  @Autowired
  ABNTestController controller;
  @Autowired
  ABNTestService service;
  @Autowired
  DeploymentRepository deploymentRepository;
  @Autowired
  LibraryRepository libraryRepository;
  @Autowired
  ModelRepository modelRepository;
  @Autowired
  ModelMetadataRepository modelMetadataRepository;
  @Autowired
  ResourceRepository resourceRepository;
  @MockBean
  KubernetesService kubernetesService;
  @MockBean
  ABNTestKubernetesService abTestKubernetesService;
  @Autowired
  ObjectMapper objectMapper;

  Library library;
  Model model;
  ModelMetadata modelMetadata;
  Resource resource;
  Deployment a;
  Deployment b;

  @BeforeEach
  public void setUp() {
    given(abTestKubernetesService.deployABNTest(any())).willReturn(true);
    given(abTestKubernetesService.deleteABNTest(any())).willReturn(true);

    library = libraryRepository.save(Library.builder().name("torch-test").build());

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
        .deviceType("cpu")
        .model(model)
        .build());

    resource = resourceRepository.save(Resource.builder()
        .name("ABTestServiceTest")
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
        .standard(Standard.LATENCY)
        .standardValue(20)
        .maxReplicas(10)
        .minReplicas(1)
        .modelMetadata(modelMetadata)
        .endPoint("a.so1s.io")
        .resource(resource)
        .build());

    b = deploymentRepository.save(Deployment.builder()
        .name("bDeployment")
        .status(Status.PENDING)
        .standard(Standard.LATENCY)
        .standardValue(20)
        .maxReplicas(10)
        .minReplicas(1)
        .modelMetadata(modelMetadata)
        .endPoint("b.so1s.io")
        .resource(resource)
        .build());
  }

  @Test
  @DisplayName("ABN 테스트를 생성하고 삭제할 수 있다.")
  public void deployAndDelete() {

    ABNTestRequestDto requestDto = ABNTestRequestDto.builder()
        .name("example")
        .domain("so1s.io")
        .elements(List.of(ABNTestElementDto.builder().deploymentId(1L).weight(1).build(),
            ABNTestElementDto.builder().deploymentId(2L).weight(1).build()))
        .build();

    var createResponse = controller.createABNTest(requestDto).getBody();
    var entity = createResponse.getEntity();

    assertThat(createResponse.getSuccess()).isTrue();
    assertThat(entity).isNotNull();
    assertThat(entity.getName()).isEqualTo("example");
    assertThat(entity.getDomain()).isEqualTo("so1s.io");
    assertThat(entity.getElements()).hasSize(2);

    var deleteResponse = controller.deleteABNTest(createResponse.getEntity().getId()).getBody();

    assertThat(deleteResponse.getSuccess()).isTrue();
    assertThat(deleteResponse.getMessage()).isNotNull();

  }

}
