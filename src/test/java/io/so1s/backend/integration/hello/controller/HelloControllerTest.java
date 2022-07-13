package io.so1s.backend.integration.hello.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(profiles = {"test"})
public class HelloControllerTest {

  private static final String helloEndPoint = "/api/v1/hello";

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("계정이 인증되지 않았을 경우에는 401 에러가 발생한다.")
  void testApiAuth() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
            .post(helloEndPoint)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()));

  }


}
