package io.so1s.backend.integration.healthcheck.controller;


import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.so1s.backend.unit.kubernetes.config.TestKubernetesConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestKubernetesConfig.class})
@ActiveProfiles(profiles = {"test"})
class HealthCheckControllerTest {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(
      HealthCheckControllerTest.class);
  private final String healthCheckEndPoint = "/livez";
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;


  @DisplayName("모델을 API를 통해 업로드 할 수 있다.")
  @Test
  void testModelUploadApi() throws Exception {
    //given

    //when
    MvcResult result = mockMvc.perform(RestDocumentationRequestBuilders
            .get(healthCheckEndPoint))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(document("health-check",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .description("현재 API의 상태를 확인합니다.")
                    .summary("API 헬스 체크")
                    .responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
                    )
                    .build()
            )))
        .andReturn();

    //then
    assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

  }
}
