package io.so1s.backend.unit.kubernetes.utils;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.so1s.backend.domain.kubernetes.utils.JobStatusChecker;
import io.so1s.backend.domain.library.entity.Library;
import io.so1s.backend.domain.library.repository.LibraryRepository;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.global.config.JpaConfig;
import io.so1s.backend.global.utils.HashGenerator;
import io.so1s.backend.global.vo.Status;
import io.so1s.backend.unit.kubernetes.config.TestKubernetesConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {TestKubernetesConfig.class})
@Import(JpaConfig.class)
@ActiveProfiles(profiles = {"test"})
@ExtendWith(MockitoExtension.class)
public class JobStatusCheckerTest {

  @Autowired
  JobStatusChecker jobStatusChecker;
  @Autowired
  KubernetesClient client;

  @Autowired
  ModelRepository modelRepository;
  @Autowired
  ModelMetadataRepository modelMetadataRepository;
  @Autowired
  LibraryRepository libraryRepository;

  @Test
  @DisplayName("인퍼런스 서버 빌드 잡이 성공했을때 ModelMetadata는 SUCCDEEDED를 Status로 가진다.")
  public void checkJobStatusSucceededTest() throws Exception {
    // given
    ModelMetadata modelMetadata = getModelMetadata();

    String jobName = "testJob";
    String namespace = "default";

    JobStatus jobStatus = new JobStatus();
    jobStatus.setSucceeded(1);

    Map<String, String> labels = new HashMap<>();
    labels.put("app", "inference-build");
    labels.put("name", jobName);

    Job job = new JobBuilder()
        .withApiVersion("batch/v1")
        .withNewMetadata()
        .withName(jobName)
        .withNamespace(namespace)
        .addToLabels(labels)
        .endMetadata()
        .withStatus(jobStatus)
        .build();
    client.batch().v1().jobs().inNamespace(namespace).createOrReplace(job);

    // when
    jobStatusChecker.checkJobStatusSync(jobName, namespace, modelMetadata);

    // then
    Assertions.assertThat(modelMetadata.getStatus()).isEqualTo(Status.SUCCEEDED);
  }

  @Test
  @DisplayName("인퍼런스 서버 빌드 잡이 실패했을때 ModelMetadata는 FAILED를 Status로 가진다.")
  public void checkJobStatusFailedTest() throws Exception {
    // given
    ModelMetadata modelMetadata = getModelMetadata();

    String jobName = "testJob2";
    String namespace = "default";

    JobStatus jobStatus = new JobStatus();
    jobStatus.setFailed(1);

    Map<String, String> labels = new HashMap<>();
    labels.put("app", "inference-build");
    labels.put("name", jobName);

    Job job = new JobBuilder()
        .withApiVersion("batch/v1")
        .withNewMetadata()
        .withName(jobName)
        .withNamespace(namespace)
        .addToLabels(labels)
        .endMetadata()
        .withStatus(jobStatus)
        .build();

    client.batch().v1().jobs().inNamespace(namespace).createOrReplace(job);

    // when
    jobStatusChecker.checkJobStatusSync(jobName, namespace, modelMetadata);

    // then
    Assertions.assertThat(modelMetadata.getStatus()).isEqualTo(Status.FAILED);
  }

  public Model getModel() {
    Library result = libraryRepository.findByName("tensorflow")
        .orElseGet(() -> libraryRepository.save(Library.builder().name("tensorflow").build()));
    return modelRepository.save(Model.builder()
        .name(UUID.randomUUID().toString())
        .library(result)
        .build());
  }

  public ModelMetadata getModelMetadata() {
    return modelMetadataRepository.save(ModelMetadata.builder()
        .status(Status.UNKNOWN)
        .version(HashGenerator.sha256())
        .fileName("testFile")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .deviceType("cpu")
        .model(getModel())
        .build());
  }
}
