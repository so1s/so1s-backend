package io.so1s.backend.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.domain.model.dto.response.ModelUploadResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ModelControllerTest {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(
      ModelControllerTest.class);
  private final String endPointPrefix = "/api/v1";
  private final String modelUploadEndPoint = String.format("%s/models", endPointPrefix);
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;


  @DisplayName("모델을 API를 통해 업로드 할 수 있다. - 1")
  @Test
  void testModelUploadApi() throws Exception {
    //given
    ModelUploadRequestDto modelUploadRequestDto = ModelUploadRequestDto.builder()
        .modelName("test")
        .url("https://example.aws.com")
        .info("test")
        .version("v1")
        .build();

    String requestBody = objectMapper.writeValueAsString(modelUploadRequestDto);

    log.info(requestBody);

    //when
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders
            .post(modelUploadEndPoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andReturn();

    //then
    String content = result.getResponse().getContentAsString();

    ModelUploadResponseDto modelUploadResponseDto = objectMapper.readValue(content,
        ModelUploadResponseDto.class);

    assertEquals(true, modelUploadResponseDto.getSuccess());
    assertEquals(modelUploadRequestDto.getModelName(), modelUploadResponseDto.getModelName());

  }


  @DisplayName("모델을 API를 통해 업로드 할 수 있다. - 2")
  @Test
  void testModelUploadApi2() throws Exception {
    //given
    ModelUploadRequestDto modelUploadRequestDto = ModelUploadRequestDto.builder()
        .modelName("test")
        .url("https://example.aws.com")
        .info("test")
        .version("v1")
        .build();

    ModelUploadResponseDto modelUploadResponseDto = ModelUploadResponseDto.builder()
        .success(true)
        .modelName("test")
        .build();

    String requestBody = objectMapper.writeValueAsString(modelUploadRequestDto);
    String responseBodyExpect = objectMapper.writeValueAsString(modelUploadResponseDto);

    log.info(requestBody);

    //when
    ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
        .post(modelUploadEndPoint)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody));

    //then
    perform.andExpect(MockMvcResultMatchers.status().isCreated());
    perform.andExpect(MockMvcResultMatchers.content().string(responseBodyExpect));
  }
}