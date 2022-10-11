package io.so1s.backend.integration.global.error;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import io.so1s.backend.domain.auth.dto.request.LoginRequestDto;
import io.so1s.backend.domain.auth.dto.response.TokenResponseDto;
import io.so1s.backend.domain.auth.entity.User;
import io.so1s.backend.domain.auth.service.UserService;
import io.so1s.backend.domain.auth.vo.UserRole;
import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.library.repository.LibraryRepository;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.global.error.exception.ErrorCode;
import io.so1s.backend.global.utils.JsonMapper;
import java.io.FileInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
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
// Flush DB after each test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// For use test jwt secret
@ActiveProfiles(profiles = {"test"})
public class GlobalExceptionHandlerTest {

  static final String signInEndPoint = "/api/v1/signin";
  static final String helloEndPoint = "/api/v1/hello";

  @Autowired
  MockMvc mockMvc;
  @Autowired
  UserService userService;
  @Autowired
  JsonMapper jsonMapper;
  @Autowired
  ModelRepository modelRepository;
  @Autowired
  LibraryRepository libraryRepository;

  User user;
  String token;

  String modelName;
  String libraryName;
  String inputShape;
  String inputDtype;
  String outputShape;
  String outputDtype;


  @BeforeEach
  public void setup() throws Exception {
    modelName = "testModel";
    libraryName = "tensorflow";
    inputShape = "float32";
    inputDtype = "(10,)";
    outputShape = "float32";
    outputDtype = "(1,)";

    String username = "so1s";
    UserRole userRole = UserRole.OWNER;
    String password = "so1s1234567890";

    createUser(username, password, userRole);
    getToken(username, password);
  }


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
  @DisplayName("잘못된 값을 전달하여 데이터 바인딩 도중 Validation 체크에 걸릴경우 INVALID_INPUT_VALUE이 발생한다.")
  public void methodArgumentNotValidExceptionTest() throws Exception {
    // given
    DeploymentRequestDto deploymentRequestDto = DeploymentRequestDto.builder()
        .name(" ")
        .modelMetadataId(1L)
        .strategy("rolling")
        .build();
    String requestDto = jsonMapper.asJsonString(deploymentRequestDto);

    // when & then
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/v1/deployments")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestDto)
            .with(csrf()).header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token)))
        .andDo(print());

    result.andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()))
        .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
        .andExpect(jsonPath("$.status").value(ErrorCode.INVALID_INPUT_VALUE.getStatus()))
        .andExpect(status().is(ErrorCode.INVALID_INPUT_VALUE.getStatus()));
  }

  @Test
  @DisplayName("모델파일을 비우고 전달하면 INVALID_INPUT_VALUE이 발생한다.")
  public void bindExceptionTest() throws Exception {
    // given
    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
        .multipart("/api/v1/models")
//        .file(multipartFile)
        .param("name", modelName)
        .param("library", libraryName)
        .param("inputShape", inputShape)
        .param("inputDtype", inputDtype)
        .param("outputShape", outputShape)
        .param("outputDtype", outputDtype)
        .with(csrf()).header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token)));

    // then
    result.andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()))
        .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
        .andExpect(jsonPath("$.status").value(ErrorCode.INVALID_INPUT_VALUE.getStatus()))
        .andExpect(status().is(ErrorCode.INVALID_INPUT_VALUE.getStatus()));
  }

  @Test
  @DisplayName("지원하지 않는 HTTP Method를 호출하면 METHOD_NOT_ALLOWED이 발생한다.")
  public void httpRequestMethodNotSupportedExceptionTest() throws Exception {
    // given
    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
        .patch("/api/v1/models")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .with(csrf()).header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token)));

    // then
    result.andExpect(jsonPath("$.message").value(ErrorCode.METHOD_NOT_ALLOWED.getMessage()))
        .andExpect(jsonPath("$.code").value(ErrorCode.METHOD_NOT_ALLOWED.getCode()))
        .andExpect(jsonPath("$.status").value(ErrorCode.METHOD_NOT_ALLOWED.getStatus()))
        .andExpect(status().is(ErrorCode.METHOD_NOT_ALLOWED.getStatus()));
  }

  @Test
  @DisplayName("잘못된 계정정보로 로그인을 시도하면 HANDLE_ACCESS_DENIED가 발생한다.")
  void badCredentialsExceptionTest() throws Exception {
    // given
    LoginRequestDto loginRequestDto = LoginRequestDto.builder()
        .username("wrong-id")
        .password("wrong-password")
        .build();
    String requestBody = jsonMapper.asJsonString(loginRequestDto);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/signin")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .with(csrf()).header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token)));

    // then
    result.andExpect(jsonPath("$.message").value(ErrorCode.HANDLE_ACCESS_DENIED.getMessage()))
        .andExpect(jsonPath("$.code").value(ErrorCode.HANDLE_ACCESS_DENIED.getCode()))
        .andExpect(jsonPath("$.status").value(ErrorCode.HANDLE_ACCESS_DENIED.getStatus()))
        .andExpect(status().is(ErrorCode.HANDLE_ACCESS_DENIED.getStatus()));
  }


  @Test
  @DisplayName("So1s 프로젝트의 예외(모델이름중복 등)가 발생하면 ApplicationException(ENTITY_DUPLICATED)이 발생한다.")
  public void applicationExceptionTest() throws Exception {
    // given
    modelRepository.save(Model.builder()
        .name(modelName)
        .library(libraryRepository.findByName(libraryName).get())
        .build());
    MockMultipartFile multipartFile = new MockMultipartFile(
        "modelFile",
        "titanic_e500.h5",
        "application/octet-stream",
        new FileInputStream("forTest/titanic_e500.h5"));

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .multipart("/api/v1/models")
            .file(multipartFile)
            .param("name", modelName)
            .param("library", libraryName)
            .param("inputShape", inputShape)
            .param("inputDtype", inputDtype)
            .param("outputShape", outputShape)
            .param("outputDtype", outputDtype)
            .with(csrf()).header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token)))
        .andDo(print());

    // then
    result.andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.code").value(ErrorCode.ENTITY_DUPLICATED.getCode()))
        .andExpect(jsonPath("$.status").value(ErrorCode.ENTITY_DUPLICATED.getStatus()))
        .andExpect(status().is(ErrorCode.ENTITY_DUPLICATED.getStatus()));
  }
}
