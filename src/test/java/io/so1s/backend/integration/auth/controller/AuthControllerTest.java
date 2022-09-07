package io.so1s.backend.integration.auth.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import io.so1s.backend.domain.auth.dto.request.LoginRequestDto;
import io.so1s.backend.domain.auth.dto.request.SignUpRequestDto;
import io.so1s.backend.domain.auth.dto.response.SignUpResponseDto;
import io.so1s.backend.domain.auth.dto.response.TokenResponseDto;
import io.so1s.backend.domain.auth.entity.User;
import io.so1s.backend.domain.auth.entity.UserToRole;
import io.so1s.backend.domain.auth.service.UserService;
import io.so1s.backend.domain.auth.vo.UserRole;
import io.so1s.backend.global.utils.JsonMapper;
import java.util.List;
import java.util.Optional;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(SpringExtension.class)
@SpringBootTest
// Flush DB after each test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// For use test jwt secret
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

  User createUser(String username, String password, UserRole userRole) throws Exception {
    // given
    user = userService.createUser(username, password, userRole);

    return user;
  }

  String getToken(String username, String password) throws Exception {
    LoginRequestDto loginRequestDto = LoginRequestDto.builder().username(username)
        .password(password)
        .build();

    String requestBody = jsonMapper.asJsonString(loginRequestDto);

    // when
    MvcResult result = mockMvc.perform(RestDocumentationRequestBuilders
            .post(signInEndPoint)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        // then
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(document("sign-in",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .description("로그인하여 토큰을 발급받습니다.")
                    .summary("로그인")
                    .requestFields(
                        fieldWithPath("username").type(JsonFieldType.STRING).description("사용자명"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                    )
                    .responseFields(
                        fieldWithPath("token").type(JsonFieldType.STRING)
                            .description("추후 인증에 사용될 JWT 토큰")
                    )
                    .build()
            )))
        .andReturn();

    // HelloController 200 test

    // given
    TokenResponseDto tokenResponseDto = jsonMapper.fromMvcResult(result, TokenResponseDto.class);

    assertThat(tokenResponseDto.getToken()).isNotBlank();

    token = tokenResponseDto.getToken();

    // when
    mockMvc.perform(RestDocumentationRequestBuilders
            .get(helloEndPoint)
            .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
            .accept(MediaType.APPLICATION_JSON))
        //then
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(document("hello-world",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .description("사용자 토큰 인증 여부를 확인합니다.")
                    .summary("사용자 인증 확인")
                    .requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer JWT 토큰")
                    )
                    .responseFields(
                        fieldWithPath("comment").type(JsonFieldType.STRING).description("메시지")
                    )
                    .build()
            )));

    return token;
  }

  @Test
  @DisplayName("기존에 생성된 Owner Role 계정을 통해 로그인할 수 있다.")
  void testSignIn() throws Exception {

    List<String> usernames = List.of("owner1", "admin1", "user1");
    List<UserRole> userRoles = List.of(UserRole.OWNER, UserRole.ADMIN, UserRole.USER);
    String password = "so1s1234567890";

    for (int i = 0; i < usernames.size(); i++) {
      String username = usernames.get(i);
      UserRole userRole = userRoles.get(i);

      createUser(username, password, userRole);
      getToken(username, password);
    }
  }

  @Test
  @DisplayName("기존에 생성된 Owner Role 계정이 Admin Role 계정을 생성할 수 있다.")
  void testSignUp() throws Exception {
    // given
    List<String> usernames = List.of("owner1", "admin1");
    List<String> newUsernames = List.of("owner2", "admin2", "user2");
    List<UserRole> userRoles = List.of(UserRole.OWNER, UserRole.ADMIN, UserRole.USER);
    String password = "so1s1234567890";

    createUser("owner", password, UserRole.OWNER);
    getToken("owner", password);

    for (int i = 1; i < usernames.size() + 1; i++) {
      String username = usernames.get(i - 1);
      UserRole userRole = userRoles.get(i - 1);

      String newUsername = newUsernames.get(i);

      createUser(username, password, userRole);
      getToken(username, password);

      SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
          .username(newUsername)
          .password(password)
          .build();

      String requestBody = jsonMapper.asJsonString(signUpRequestDto);

      // when
      ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
          .post(signUpEndPoint)
          .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody));

      HttpStatus status = i == 3 ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED;

      // then
      MvcResult mvcResult = resultActions.andExpect(
              MockMvcResultMatchers.status().is(status.value()))
          .andReturn();

      if (i == 3) {
        continue;
      }

      resultActions.andDo(document("sign-up",
          preprocessRequest(prettyPrint()),
          preprocessResponse(prettyPrint()),
          resource(
              ResourceSnippetParameters.builder()
                  .description("현재 사용자가 자신보다 낮은 권한을 가진 사용자를 생성합니다.")
                  .summary("사용자 생성")
                  .requestHeaders(
                      headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer JWT 토큰")
                  )
                  .responseFields(
                      fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
                  )
                  .build()
          )));

      SignUpResponseDto signUpResponseDto = jsonMapper.fromMvcResult(mvcResult,
          SignUpResponseDto.class);

      assertThat(signUpResponseDto.getSuccess()).isTrue();

      Optional<User> optionalCreatedUser = userService.findByUsername(newUsername);

      assertThat(optionalCreatedUser).isPresent();

      User createdUser = optionalCreatedUser.get();
      UserRole newUserRole = userRoles.get(i);

      assertThat(createdUser.getUsername()).isEqualTo(newUsername);
      assertThat(createdUser.getUserToRoles().stream().map(UserToRole::getUserRole))
          .contains(newUserRole);

      user = createdUser;
    }

  }
}
