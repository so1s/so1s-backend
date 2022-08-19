package io.so1s.backend.unit.model.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import io.so1s.backend.domain.aws.service.FileUploadService;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.model.controller.ModelController;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelDetailResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelFindResponseDto;
import io.so1s.backend.domain.model.dto.response.ModelMetadataFindResponseDto;
import io.so1s.backend.domain.model.entity.Library;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.service.ModelServiceImpl;
import io.so1s.backend.global.config.SecurityConfig;
import io.so1s.backend.global.utils.HashGenerator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  FileUploadService fileUploadService;
  @MockBean
  KubernetesService kubernetesService;

  ObjectMapper objectMapper;
  ModelUploadRequestDto modelUploadRequestDto;
  String requestDtoMapped;
  FileSaveResultForm saveResult;
  Model model;

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
    model = Model.builder()
        .name(modelUploadRequestDto.getName())
        .library(Library.builder()
            .name(modelUploadRequestDto.getLibrary())
            .build())
        .build();

  }

  @Test
  @DisplayName("모델을 업로드 한다.")
  public void uploadTest() throws Exception {
    // given
    // setup()
    String version = HashGenerator.sha256();
    when(modelService.createModel(any(ModelUploadRequestDto.class))).thenReturn(model);
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


  @Test
  @DisplayName("기존의 모델 버전을 업데이트 한다.")
  public void updateTest() throws Exception {
    // given
    // setup()
    String version = HashGenerator.sha256();
    when(modelService.findModelByName(any())).thenReturn(model);
    when(fileUploadService.uploadFile(any())).thenReturn(saveResult);
    when(modelService.createModelMetadata(any(Model.class), any(ModelUploadRequestDto.class), any(
        FileSaveResultForm.class))).thenReturn(ModelMetadata.builder()
        .version(version)
        .build());
    when(kubernetesService.inferenceServerBuild(any(ModelMetadata.class))).thenReturn(Boolean.TRUE);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .put("/api/v1/models")
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

  @Test
  @DisplayName("모델들을 성공적으로 조회하면 200을 반환한다.")
  public void findModelsTest() throws Exception {
    // given
    List<ModelFindResponseDto> findModels = new ArrayList<>();
    findModels.add(ModelFindResponseDto.builder()
        .age(LocalDateTime.now())
        .name("testModel")
        .status("usable")
        .version(HashGenerator.sha256())
        .library("tensorflow")
        .build());
    when(modelService.findModels()).thenReturn(findModels);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .get("/api/v1/models")
            .accept(MediaType.APPLICATION_JSON)
            .with(csrf()))
        .andDo(print());

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$[0].age").value(findModels.get(0).getAge().toString()))
        .andExpect(jsonPath("$[0].name").value(findModels.get(0).getName()))
        .andExpect(jsonPath("$[0].status").value(findModels.get(0).getStatus()))
        .andExpect(jsonPath("$[0].version").value(findModels.get(0).getVersion()))
        .andExpect(jsonPath("$[0].library").value(findModels.get(0).getLibrary()));
  }

  @Test
  @DisplayName("특정 모델의 버전들을 성공적으로 조회하면 200을 반환한다.")
  public void findModelMetadatasTest() throws Exception {
    // given
    List<ModelMetadataFindResponseDto> findModelMetadatas = new ArrayList<>();
    findModelMetadatas.add(ModelMetadataFindResponseDto.builder()
        .age(LocalDateTime.now())
        .version(HashGenerator.sha256())
        .status("usable")
        .url("http://s3.test.com/")
        .build());
    when(modelService.findModelMetadatasByModelId(any())).thenReturn(findModelMetadatas);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .get("/api/v1/models/1")
            .accept(MediaType.APPLICATION_JSON)
            .with(csrf()))
        .andDo(print());

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$[0].age").value(findModelMetadatas.get(0).getAge().toString()))
        .andExpect(jsonPath("$[0].version").value(findModelMetadatas.get(0).getVersion()))
        .andExpect(jsonPath("$[0].status").value(findModelMetadatas.get(0).getStatus()))
        .andExpect(jsonPath("$[0].url").value(findModelMetadatas.get(0).getUrl()));
  }

  @Test
  @DisplayName("특정 모델의 특정 버전의 세부사항을 성공적으로 조회하면 200을 반환한다.")
  public void findModelMetadataTest() throws Exception {
    // given
    ModelDetailResponseDto findModelMetadata = ModelDetailResponseDto.builder()
        .age(LocalDateTime.now())
        .name("testModel")
        .version(HashGenerator.sha256())
        .status("usable")
        .url("http://s3.test.com/")
        .library("tensorflow")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .build();
    when(modelService.findModelDetail(any(), any())).thenReturn(findModelMetadata);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .get("/api/v1/models/1/versions/" + HashGenerator.sha256())
            .with(csrf()))
        .andDo(print());

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.age").value(findModelMetadata.getAge().toString()))
        .andExpect(jsonPath("$.name").value(findModelMetadata.getName()))
        .andExpect(jsonPath("$.version").value(findModelMetadata.getVersion()))
        .andExpect(jsonPath("$.status").value(findModelMetadata.getStatus()))
        .andExpect(jsonPath("$.url").value(findModelMetadata.getUrl()))
        .andExpect(jsonPath("$.library").value(findModelMetadata.getLibrary()))
        .andExpect(jsonPath("$.inputShape").value(findModelMetadata.getInputShape()))
        .andExpect(jsonPath("$.inputDtype").value(findModelMetadata.getInputDtype()))
        .andExpect(jsonPath("$.outputShape").value(findModelMetadata.getOutputShape()))
        .andExpect(jsonPath("$.outputDtype").value(findModelMetadata.getOutputDtype()));
  }
}