package io.so1s.backend.integration.model.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.domain.model.service.ModelServiceImpl;
import io.so1s.backend.global.utils.HashGenerator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@EnableKubernetesMockClient(crud = true)
@SpringBootTest
@ActiveProfiles(profiles = {"test"})
class ModelServiceTest {

  @Autowired
  ModelServiceImpl modelService;
  @Autowired
  ModelRepository modelRepository;
  @Autowired
  ModelMetadataRepository modelMetadataRepository;

  String version;
  String name = "testModel";
  String url = "http://s3.test.com/";
  String library = "tensorflow";
  String info = "this is test model.";

  ModelUploadRequestDto modelUploadRequestDto;

  @BeforeEach
  public void setup() {
    version = HashGenerator.sha256();
    modelUploadRequestDto = ModelUploadRequestDto.builder()
        .name(name)
        .url(url)
        .library(library)
        .info(info)
        .build();
  }

  @Test
  @Transactional
  public void createModelTest() throws Exception {
    // given

    // when
    Model model = modelService.createModel(modelUploadRequestDto);
    Optional<Model> result = modelRepository.findById(model.getId());

    // then
    assertThat(model).isEqualTo(result.get());
    assertThat(model.getId()).isEqualTo(result.get().getId());
    assertThat(model.getName()).isEqualTo(result.get().getName());
    assertThat(model.getLibrary()).isEqualTo(result.get().getLibrary());

  }

  @Test
  @Transactional
  public void creteModelMetadataTest() throws Exception {
    // given
    Model model = modelUploadRequestDto.toEntity();

    // when
    ModelMetadata modelMetadata = modelService.createModelMetadata(model, modelUploadRequestDto);
    Optional<ModelMetadata> result = modelMetadataRepository.findById(modelMetadata.getId());

    // then
    assertThat(modelMetadata).isEqualTo(result.get());
    assertThat(modelMetadata.getId()).isEqualTo(result.get().getId());
    assertThat(modelMetadata.getInfo()).isEqualTo(result.get().getInfo());
    assertThat(modelMetadata.getVersion()).isEqualTo(result.get().getVersion());
    assertThat(modelMetadata.getStatus()).isEqualTo(result.get().getStatus());
    assertThat(modelMetadata.getUrl()).isEqualTo(result.get().getUrl());
    assertThat(modelMetadata.getModel()).isEqualTo(result.get().getModel());

  }
}