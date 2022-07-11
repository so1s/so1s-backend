package io.so1s.backend.integration.healthcheck.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(SpringExtension.class)
@SpringBootTest
class HealthCheckControllerTest {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(
      HealthCheckControllerTest.class);
  private final String healthCheckEndPoint = "/livez";
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;


  @DisplayName("모델을 API를 통해 업로드 할 수 있다. - 1")
  @Test
  void testModelUploadApi() throws Exception {
    //given

    //when
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders
            .get(healthCheckEndPoint))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    //then
    assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

  }
}
