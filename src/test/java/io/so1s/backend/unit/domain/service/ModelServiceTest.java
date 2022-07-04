package io.so1s.backend.unit.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.domain.model.service.ModelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

  @InjectMocks
  ModelService modelService;

  @Mock
  ModelRepository modelRepository;
  @Mock
  ModelMetadataRepository modelMetadataRepository;


  @Test
  void testSave() {
    // given
    ModelUploadRequestDto modelUploadRequestDto = ModelUploadRequestDto
        .builder().modelName("ubuntu").url("test://").info("test").version("v1").build();
    Model model = Model.builder().name("ubuntu").build();
    ModelMetadata modelMetadata = ModelMetadata.builder()
        .model(model).url("test://").info("test").version("v1").build();

    given(modelRepository.findByName(anyString())).willReturn(model);
    given(modelRepository.saveAndFlush(any())).willReturn(model);
    given(modelMetadataRepository.saveAndFlush(any())).willReturn(modelMetadata);

    // when
    ModelUploadResponseDto modelUploadResponseDto = modelService.save(modelUploadRequestDto);

    // then
    assertThat(modelUploadResponseDto.getModelName()).isEqualTo(model.getName());
    assertThat(modelUploadResponseDto.getSuccess()).isTrue();
  }
}
