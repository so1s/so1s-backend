package io.so1s.backend.integration.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.so1s.backend.domain.auth.dto.request.LoginRequestDto;
import io.so1s.backend.domain.auth.dto.request.SignUpRequestDto;
import io.so1s.backend.domain.auth.dto.response.SignUpResponseDto;
import io.so1s.backend.domain.auth.dto.response.TokenResponseDto;
import io.so1s.backend.domain.auth.entity.User;
import io.so1s.backend.domain.auth.service.UserService;
import io.so1s.backend.domain.auth.vo.UserRole;
import io.so1s.backend.global.utils.JsonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(profiles = {"test"})
class AuthControllerTest {

  private static final String signInEndPoint = "/api/v1/signin";
  private static final String signUpEndPoint = "/api/v1/signup";

  private static final String helloEndPoint = "/api/v1/hello";

  @Autowired
  private UserService userService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JsonMapper jsonMapper;

  private User user;

  private String token;

  @Test
  @DisplayName("기존에 생성된 Owner Role 계정을 통해 로그인할 수 있다.")
  void testSignIn() throws Exception {
    // given
    user = userService.createUser("owner", "so1s", UserRole.OWNER);

    LoginRequestDto loginRequestDto = LoginRequestDto.builder().username("owner").password("so1s")
        .build();

    String requestBody = jsonMapper.asJsonString(loginRequestDto);

    // when
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders
            .post(signInEndPoint)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        // then
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    // HelloController 200 test

    // given
    TokenResponseDto tokenResponseDto = jsonMapper.fromMvcResult(result, TokenResponseDto.class);

    assertThat(tokenResponseDto.getToken()).isNotBlank();

    token = tokenResponseDto.getToken();

    // when
    mockMvc.perform(MockMvcRequestBuilders
            .get(helloEndPoint)
            .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
            .accept(MediaType.APPLICATION_JSON))
        //then
        .andExpect(MockMvcResultMatchers.status().isOk());

  }

  @Test
  @DisplayName("기존에 생성된 Owner Role 계정이 Admin Role 계정을 생성할 수 있다.")
  void testSignUp() throws Exception {
    // given
    testSignIn();

    SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
        .username("admin")
        .password("so1s")
        .build();

    String requestBody = jsonMapper.asJsonString(signUpRequestDto);

    // when
    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
            .post(signUpEndPoint)
            .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
        .andReturn();

    // then
    SignUpResponseDto signUpResponseDto = jsonMapper.fromMvcResult(mvcResult,
        SignUpResponseDto.class);

    assertThat(signUpResponseDto.getSuccess()).isTrue();

    assertThat(userService.findByUsername("admin").isPresent()).isTrue();
  }
}
