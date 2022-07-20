package io.so1s.backend.unit.model.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.domain.model.service.ModelServiceImpl;
import io.so1s.backend.global.utils.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@EnableKubernetesMockClient(crud = true)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = {"test"})
class ModelServiceTest {

  KubernetesClient client;


  ModelServiceImpl modelService;
  KubernetesService kubernetesService;
  @Mock
  ModelRepository modelRepository;
  @Mock
  ModelMetadataRepository modelMetadataRepository;

  String version;
  String name = "testModel";
  String url = "http://s3.test.com/";
  String library = "tensorflow";
  String info = "this is test model.";

  @BeforeEach
  public void setup() {
    version = HashGenerator.sha1();
    kubernetesService = new KubernetesService(client);
    modelService = new ModelServiceImpl(modelRepository, modelMetadataRepository,
        kubernetesService);
  }

  @Test
  @DisplayName("저장한 모델을 인퍼런스서버로 빌드한다.")
  public void modelUpload() throws Exception {
    // given
    ModelMetadata modelMetadata = ModelMetadata.builder()
        .url(url)
        .version(version)
        .info(info)
        .status("pending")
        .model(Model.builder()
            .name(name)
            .library(library)
            .build())
        .build();

    // when
    ModelUploadResponseDto result = modelService.buildModel(modelMetadata);

    //then
    assertThat(result.getSuccess()).isTrue();
    assertThat(modelMetadata.getModel().getName()).isEqualTo(result.getName());
    assertThat(modelMetadata.getVersion()).isEqualTo(result.getVersion());
  }
}