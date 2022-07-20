package io.so1s.backend.unit.model.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.so1s.backend.domain.model.controller.ModelController;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
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

  ObjectMapper objectMapper;
  ModelUploadRequestDto requestDto;
  String requestDtoMapped;
  ModelUploadResponseDto responseDto;

  @BeforeEach
  public void setup() throws Exception {
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
    objectMapper = new ObjectMapper();
    requestDtoMapped = objectMapper.writeValueAsString(requestDto);
    String version = HashGenerator.sha256();
    responseDto = ModelUploadResponseDto.builder()
        .name(name)
        .success(Boolean.TRUE)
        .version(version)
        .build();
  }

  @Test
  @DisplayName("모델을 업로드 한다.")
  public void upload() throws Exception {
    // given
    when(modelService.buildModel(any())).thenReturn(responseDto);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/models")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestDtoMapped)
        .with(csrf()));

    //then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value("true"))
        .andExpect(jsonPath("$.name").value(requestDto.getName()))
        .andDo(print());
  }
}