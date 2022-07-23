package io.so1s.backend.unit.kubernetes.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.global.utils.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@EnableKubernetesMockClient(crud = true)
@ExtendWith(MockitoExtension.class)
@WithMockUser
@ActiveProfiles(profiles = {"test"})
public class KubernetesServiceTest {

  KubernetesClient client;
  KubernetesService kubernetesService;

  @BeforeEach
  public void setup() {
    kubernetesService = new KubernetesService(client);
  }

  @Test
  @DisplayName("성공적으로 인퍼런스 잡이 실행되면 true를 반환한다.")
  public void inferenceServerBuild() throws Exception {
    // given
    ModelMetadata modelMetadata = ModelMetadata.builder()
        .status("running")
        .version(HashGenerator.sha256())
        .fileName("e8eb72cd-1ef4-45f4-8105-2f4c5357e4a8.h5")
        .url("https://so1s.s3.ap-northeast-2.amazonaws.com/13df693e-bb11-404c-9cfd-c5b1a1ecdc43.h5")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputShape("float32")
        .model(Model.builder()
            .name("FinetunedModel")
            .library("torch")
            .build())
        .build();

    // when
    boolean result = kubernetesService.inferenceServerBuild(modelMetadata);

    // then
    assertThat(result).isTrue();

  }
}
