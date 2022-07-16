package io.so1s.backend.unit.model.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.domain.model.service.ModelServiceImpl;
import io.so1s.backend.global.utils.HashGenerator;
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

  String name = "testModel";
  String url = "http://s3.test.com/";
  String library = "tensorflow";
  String info = "this is test model.";

  @Test
  public void modelUpload() throws Exception {
    // given
    ModelUploadRequestDto requestDto = ModelUploadRequestDto.builder()
        .name(name)
        .url(url)
        .library(library)
        .info(info)
        .build();
    String version = HashGenerator.hashGenerateBySha256();
    ModelMetadata modelMetadata = ModelMetadata.builder()
        .url(url)
        .version(version)
        .info(info)
        .status("pending")
        .build();
    when(modelRepository.save(any())).thenReturn(requestDto.toEntity());
    when(modelMetadataRepository.save(any())).thenReturn(modelMetadata);
    when(kubernetesService.inferenceServerBuild(any(), any())).thenReturn(true);

    // when
    ModelUploadResponseDto result = modelService.modelUpload(requestDto);

    //then
    assertThat(name).isEqualTo(result.getName());
    assertThat(version).isEqualTo(result.getVersion());
    assertThat(result.getSuccess()).isTrue();
  }
}