package io.so1s.backend.unit.test.repository;

import static org.assertj.core.api.Assertions.assertThat;

import io.so1s.backend.domain.deployment.dto.request.Standard;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.library.entity.Library;
import io.so1s.backend.domain.library.repository.LibraryRepository;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.repository.ResourceRepository;
import io.so1s.backend.domain.test.v2.dto.mapper.ABNTestElementMapper;
import io.so1s.backend.domain.test.v2.dto.mapper.ABNTestMapper;
import io.so1s.backend.domain.test.v2.entity.ABNTest;
import io.so1s.backend.domain.test.v2.entity.ABNTestElement;
import io.so1s.backend.domain.test.v2.repository.ABNTestElementRepository;
import io.so1s.backend.domain.test.v2.repository.ABNTestRepository;
import io.so1s.backend.global.utils.HashGenerator;
import io.so1s.backend.global.vo.Status;
import io.so1s.backend.unit.kubernetes.config.TestKubernetesConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {TestKubernetesConfig.class})
@ActiveProfiles(profiles = {"test"})
public class ABNTestRepositoryTest {

  @Autowired
  ABNTestMapper mapper;
  @Autowired
  ABNTestRepository repository;
  @Autowired
  ABNTestElementMapper elementMapper;
  @Autowired
  ABNTestElementRepository elementRepository;
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

  @Test
  @DisplayName("@ManyToMany등 연관관게, 엔티티 테스트")
  @Transactional
  public void entitiesTest() {
    // given

    Resource resource = resourceRepository.save(
        Resource.builder().name("boston").cpu("250m").cpuLimit("250m").memory("1Gi")
            .memoryLimit("1Gi").build());
    Library library = libraryRepository.save(Library.builder().name("example").build());
    Model model = modelRepository.save(Model.builder().name("boston").library(library).build());
    ModelMetadata modelMetadata = modelMetadataRepository.save(
        ModelMetadata.builder()
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
            .build()
    );
    Deployment deployment = Deployment.builder()
        .name("example")
        .status(Status.PENDING)
        .endPoint("inference-example.so1s.io")
        .standard(Standard.LATENCY)
        .standardValue(20)
        .maxReplicas(10)
        .minReplicas(1)
        .modelMetadata(modelMetadata)
        .resource(resource)
        .build();

    // when

    ABNTest abnTest = ABNTest.builder().name("example").domain("so1s.io")
        .endPoint("abn-test-example-so1s.io").build();
    ABNTestElement abnTestElement = ABNTestElement.builder().weight(1)
        .deployment(deployment).build();

    abnTest.addElement(abnTestElement);

    // then

    assertThat(abnTest.getElements()).hasSize(1);
    assertThat(abnTest.getElements().get(0)).isEqualTo(abnTestElement);

  }

}
