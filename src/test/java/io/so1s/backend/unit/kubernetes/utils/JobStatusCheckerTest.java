package io.so1s.backend.unit.kubernetes.utils;

import static org.assertj.core.api.Assertions.assertThat;

import io.fabric8.istio.mock.EnableIstioMockClient;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.so1s.backend.domain.kubernetes.utils.JobStatusChecker;
import io.so1s.backend.domain.model.entity.Library;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.LibraryRepository;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.global.config.JpaConfig;
import io.so1s.backend.global.entity.Status;
import io.so1s.backend.global.utils.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@EnableKubernetesMockClient(crud = true)
@EnableIstioMockClient(crud = true)
@DataJpaTest
@Import(JpaConfig.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = {"test"})
public class JobStatusCheckerTest {

  JobStatusChecker jobStatusChecker;
  KubernetesClient client;

  @Autowired
  ModelMetadataRepository modelMetadataRepository;
  @Autowired
  LibraryRepository libraryRepository;
  @Autowired
  ModelRepository modelRepository;

  @BeforeEach
  public void setup() {
    jobStatusChecker = new JobStatusChecker(client, modelMetadataRepository);
  }

  @Test
  @DisplayName("인퍼런스 서버 빌드 잡이 성공했을때 ModelMetadata는 SUCCDEEDED를 Status로 가진다.")
  public void checkJobStatusSucceededTest() throws Exception {
    // given
    Library lib = libraryRepository.findByName("tensorflow").get();
    Model model = modelRepository.save(Model.builder()
        .name("testModel")
        .library(lib)
        .build());
    ModelMetadata modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
        .status(Status.PENDING)
        .version(HashGenerator.sha256())
        .fileName("testModelFile")
        .url("https://s3.testModelFile.com")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .model(model)
        .build());

    String jobName = "testJob";
    String namespace = "default";
    JobStatus jobStatus = new JobStatus();
    jobStatus.setSucceeded(1);
    Job job = new JobBuilder()
        .withApiVersion("batch/v1")
        .withNewMetadata()
        .withName(jobName)
        .withNamespace(namespace)
        .addToLabels("job-name", jobName)
        .endMetadata()
        .withStatus(jobStatus)
        .build();
    client.batch().v1().jobs().inNamespace(namespace).createOrReplace(job);

    // when
    jobStatusChecker.checkJobStatus(jobName, namespace, modelMetadata);

    // then
    ModelMetadata find = modelMetadataRepository.findById(modelMetadata.getId()).get();
    assertThat(find.getStatus()).isEqualTo(Status.SUCCEEDED);
  }

  @Test
  @DisplayName("인퍼런스 서버 빌드 잡이 실패했을때 ModelMetadata는 FAILED를 Status로 가진다.")
  public void checkJobStatusFailedTest() throws Exception {
    // given
    Library lib = libraryRepository.findByName("tensorflow").get();
    Model model = modelRepository.save(Model.builder()
        .name("testModel")
        .library(lib)
        .build());
    ModelMetadata modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
        .status(Status.PENDING)
        .version(HashGenerator.sha256())
        .fileName("testModelFile")
        .url("https://s3.testModelFile.com")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .model(model)
        .build());

    String jobName = "testJob";
    String namespace = "default";
    JobStatus jobStatus = new JobStatus();
    jobStatus.setFailed(1);
    Job job = new JobBuilder()
        .withApiVersion("batch/v1")
        .withNewMetadata()
        .withName(jobName)
        .withNamespace(namespace)
        .addToLabels("job-name", jobName)
        .endMetadata()
        .withStatus(jobStatus)
        .build();
    client.batch().v1().jobs().inNamespace(namespace).createOrReplace(job);

    // when
    jobStatusChecker.checkJobStatus(jobName, namespace, modelMetadata);

    // then
    ModelMetadata find = modelMetadataRepository.findById(modelMetadata.getId()).get();
    assertThat(find.getStatus()).isEqualTo(Status.FAILED);
  }
}
