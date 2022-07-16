package io.so1s.backend.integration.model.service;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import io.so1s.backend.global.utils.JsonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
@WithMockUser
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = {"test"})
class ModelControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  private JsonMapper jsonMapper;

  @Test
  @DisplayName("모델을 업로드 한다.")
  public void upload() throws Exception {
    // given
    ModelUploadRequestDto modelUploadRequestDto = ModelUploadRequestDto.builder()
        .name("testModel")
        .url("http://s3.test.com/")
        .library("tensorflow")
        .info("this is test model.")
        .build();

    String requestDto = jsonMapper.asJsonString(modelUploadRequestDto);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/models")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestDto));

    //then

    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value("true"))
        .andExpect(jsonPath("$.name").value(modelUploadRequestDto.getName()))
        .andDo(print());
  }
}