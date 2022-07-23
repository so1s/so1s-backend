package io.so1s.backend.unit.model.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.so1s.backend.domain.aws.service.FileSaveResultForm;
import io.so1s.backend.domain.aws.service.FileUploadService;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.controller.ModelController;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.service.ModelServiceImpl;
import io.so1s.backend.global.config.SecurityConfig;
import io.so1s.backend.global.utils.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@WithMockUser
@ActiveProfiles(profiles = {"test"})
@WebMvcTest(controllers = ModelController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
    }
)
class ModelControllerTest {

  @Autowired
  MockMvc mockMvc;
  @MockBean
  ModelServiceImpl modelService;
  @MockBean
  KubernetesService kubernetesService;
  @MockBean
  FileUploadService fileUploadService;

  ObjectMapper objectMapper;
  ModelUploadRequestDto modelUploadRequestDto;
  String requestDtoMapped;
  FileSaveResultForm saveResult;

  @BeforeEach
  public void setup() throws Exception {
    objectMapper = new ObjectMapper();
    modelUploadRequestDto = ModelUploadRequestDto.builder()
        .name("testModel")
        .library("tensorflow")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build();
    requestDtoMapped = objectMapper.writeValueAsString(modelUploadRequestDto);
    saveResult = FileSaveResultForm.builder()
        .savedName("testFileName")
        .url("http://s3.test.com/")
        .build();
  }

  @Test
  @DisplayName("모델을 업로드 한다.")
  public void uploadTest() throws Exception {
    // given
    // setup()
    String version = HashGenerator.sha256();
    when(modelService.createModel(any(ModelUploadRequestDto.class))).thenReturn(
        modelUploadRequestDto.toModelEntity());
    when(fileUploadService.uploadFile(any())).thenReturn(saveResult);
    when(modelService.createModelMetadata(any(Model.class), any(ModelUploadRequestDto.class), any(
        FileSaveResultForm.class))).thenReturn(ModelMetadata.builder()
        .version(version)
        .build());
    when(kubernetesService.inferenceServerBuild(any(ModelMetadata.class))).thenReturn(Boolean.TRUE);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/v1/models")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestDtoMapped)
            .with(csrf()))
        .andDo(print());

    //then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value("true"))
        .andExpect(jsonPath("$.modelName").value(modelUploadRequestDto.getName()))
        .andExpect(jsonPath("$.version").value(version))
        .andExpect(jsonPath("$.fileName").value(saveResult.getSavedName()))
        .andExpect(jsonPath("$.savedUrl").value(saveResult.getUrl()));
  }
}