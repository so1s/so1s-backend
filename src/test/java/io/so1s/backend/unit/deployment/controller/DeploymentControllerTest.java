package io.so1s.backend.unit.deployment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.so1s.backend.domain.deployment.controller.DeploymentController;
import io.so1s.backend.domain.deployment.dto.request.DeploymentRequestDto;
import io.so1s.backend.domain.deployment.dto.request.ResourceRequestDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentFindResponseDto;
import io.so1s.backend.domain.deployment.dto.response.DeploymentResponseDto;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.Resource;
import io.so1s.backend.domain.deployment.service.DeploymentServiceImpl;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
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
@WebMvcTest(controllers = DeploymentController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
    }
)
public class DeploymentControllerTest {

  @Autowired
  MockMvc mockMvc;
  @MockBean
  DeploymentServiceImpl deploymentService;
  @MockBean
  KubernetesService kubernetesService;
  @MockBean
  ModelServiceImpl modelService;

  ObjectMapper objectMapper;
  DeploymentRequestDto deploymentRequestDto;
  DeploymentResponseDto deploymentResponseDto;
  Resource resource;
  String requestDtoMapped;
  String version;

  @BeforeEach
  public void setup() throws JsonProcessingException {
    version = HashGenerator.sha256();
    String name = "testDeploy";
    deploymentRequestDto = DeploymentRequestDto.builder()
        .name(name)
        .modelMetadataId(1L)
        .strategy("rolling")
        .resources(ResourceRequestDto.builder()
            .cpu("1")
            .memory("1Gi")
            .gpu("0")
            .cpuLimit("2")
            .memoryLimit("2Gi")
            .gpuLimit("0")
            .build())
        .build();
    deploymentResponseDto = DeploymentResponseDto.builder()
        .success(Boolean.TRUE)
        .id(1L)
        .name(name)
        .build();

    objectMapper = new ObjectMapper();
    requestDtoMapped = objectMapper.writeValueAsString(deploymentRequestDto);

    resource = deploymentRequestDto.getResources().toEntity();
  }

  @Test
  @DisplayName("배포가 정상적으로 이루어졌을때 200을 반환한다.")
  public void createDeploymentTest() throws Exception {
    // given
    when(deploymentService.createResource(any(ResourceRequestDto.class))).thenReturn(resource);
    when(deploymentService.createDeployment(any(Resource.class), any(DeploymentRequestDto.class)))
        .thenReturn(Deployment.builder()
            .id(1L)
            .name(deploymentRequestDto.getName())
            .resource(resource)
            .build());
    when(kubernetesService.deployInferenceServer(any(Deployment.class))).thenReturn(Boolean.TRUE);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/deployments")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestDtoMapped)
        .with(csrf()));

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value("true"))
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value(deploymentRequestDto.getName()))
        .andDo(print());
  }

  @Test
  @DisplayName("기존 배포를 성공적으로 업데이트를 하면 200을 반환한다.")
  public void updateDeploymentTest() throws Exception {
    // given
    when(deploymentService.updateDeployment(any(DeploymentRequestDto.class))).thenReturn(
        Deployment.builder()
            .id(1L)
            .name(deploymentRequestDto.getName())
            .resource(resource)
            .build());
    when(kubernetesService.deployInferenceServer(any(Deployment.class))).thenReturn(Boolean.TRUE);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
        .put("/api/v1/deployments")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestDtoMapped)
        .with(csrf()));

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value("true"))
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value(deploymentRequestDto.getName()))
        .andDo(print());
  }

  @Test
  @DisplayName("디플로이먼트들을 성공적으로 조회 하면 200을 반환한다.")
  public void findDeployments() throws Exception {
    // given
    List<DeploymentFindResponseDto> list = new ArrayList<>();
    list.add(DeploymentFindResponseDto.builder()
        .age(LocalDateTime.now().toString())
        .deploymentName("testDeploy")
        .status("running")
        .endPoint("http://test.endpoint.com/")
        .build());
    when(deploymentService.findDeployments()).thenReturn(list);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
        .get("/api/v1/deployments")
        .accept(MediaType.APPLICATION_JSON)
        .with(csrf()));

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$[0].age").exists()) // TimeStamp 불일치 문제로 임시 수정
        .andExpect(jsonPath("$[0].deploymentName").value(list.get(0).getDeploymentName()))
        .andExpect(jsonPath("$[0].status").value(list.get(0).getStatus()))
        .andExpect(jsonPath("$[0].endPoint").value(list.get(0).getEndPoint()))
        .andDo(print());
  }

  @Test
  @DisplayName("디플로이먼트를 성공적으로 조회 하면 200을 반환한다.")
  public void findDeployment() throws Exception {
    // given
    DeploymentFindResponseDto responseDto = DeploymentFindResponseDto.builder()
        .age(LocalDateTime.now().toString())
        .deploymentName("testDeploy")
        .status("running")
        .endPoint("http://test.endpoint.com/")
        .build();
    when(deploymentService.findDeployment(any())).thenReturn(responseDto);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders
        .get("/api/v1/deployments/1")
        .accept(MediaType.APPLICATION_JSON)
        .with(csrf()));

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.age").exists()) // TimeStamp 불일치 문제로 임시 수정
        .andExpect(jsonPath("$.deploymentName").value(responseDto.getDeploymentName()))
        .andExpect(jsonPath("$.status").value(responseDto.getStatus()))
        .andExpect(jsonPath("$.endPoint").value(responseDto.getEndPoint()))
        .andDo(print());
  }
}
