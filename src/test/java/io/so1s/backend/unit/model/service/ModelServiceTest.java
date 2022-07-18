package io.so1s.backend.unit.model.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

  @Mock
  ModelRepository modelRepository;
  @Mock
  ModelMetadataRepository modelMetadataRepository;
  @Mock
  KubernetesService kubernetesService;

  @InjectMocks
  ModelServiceImpl modelService;

  ModelUploadRequestDto requestDto;
  String version;
  Model model;
  ModelMetadata modelMetadata;

  @BeforeEach
  public void setup() {
    String name = "testModel";
    String url = "http://s3.test.com/";
    String library = "tensorflow";
    String info = "this is test model.";

    requestDto = ModelUploadRequestDto.builder()
        .name(name)
        .url(url)
        .library(library)
        .info(info)
        .build();
    model = requestDto.toEntity();
    version = HashGenerator.sha1();
    modelMetadata = ModelMetadata.builder()
        .url(url)
        .version(version)
        .info(info)
        .status("pending")
        .model(model)
        .build();
  }

  @Test
  @DisplayName("모델을 업로드 한다.")
  public void modelUpload() throws Exception {
    // given
    when(modelRepository.save(any())).thenReturn(model);
    when(modelMetadataRepository.save(any())).thenReturn(modelMetadata);
    when(kubernetesService.inferenceServerBuild(any())).thenReturn(true);

    // when
    Model resultModel = modelService.createModel(requestDto);
    ModelMetadata resultModelMetadata = modelService.createModelMetadata(resultModel, requestDto);
    ModelUploadResponseDto result = modelService.buildModel(resultModelMetadata);

    //then
    assertThat(result.getSuccess()).isTrue();
    assertThat(requestDto.getName()).isEqualTo(result.getName());
    assertThat(version).isEqualTo(result.getVersion());
  }

  @Test
  public void findModelMetadataByVersion() throws Exception {
    // given

    // when

    // then

  }
}