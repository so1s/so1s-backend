package io.so1s.backend.unit.resource.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.istio.mock.EnableIstioMockClient;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.so1s.backend.domain.resource.dto.mapper.ResourceMapper;
import io.so1s.backend.domain.resource.dto.service.ResourceDto;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.repository.ResourceRepository;
import io.so1s.backend.domain.resource.service.ResourceService;
import io.so1s.backend.domain.resource.service.ResourceServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableKubernetesMockClient(crud = true)
@ExtendWith(MockitoExtension.class)
@WithMockUser
@ActiveProfiles(profiles = {"test"})
public class ResourceServiceTest {

  @Autowired
  private ResourceRepository resourceRepository;
  private ResourceMapper resourceMapper = new ResourceMapper();
  private KubernetesClient client;
  private ResourceService resourceService;


  @BeforeEach
  void setUp() {
    ObjectMapper objectMapper = new ObjectMapper();

    resourceService = new ResourceServiceImpl(resourceRepository, resourceMapper, objectMapper,
        client);
  }

  @Nested
  @EnableKubernetesMockClient(crud = true)
  @DisplayName("Node 리소스 현황을 통해 새로 올려지는 Deployment 리소스가 사용 가능한지 확인한다.")
  class NodeResourceDeployableTest {

    @DataJpaTest
    @EnableKubernetesMockClient(crud = true)
    @EnableIstioMockClient(crud = true)
    @ExtendWith(MockitoExtension.class)
    @WithMockUser
    @Nested
    @DisplayName("ResourceService.gte 메소드의 동작을 검증한다.")
    class gteTest {

      @Test
      @DisplayName("A의 값이 B보다 클 때는 true를 반환한다.")
      void greaterThanTest() throws Exception {
        Quantity a = new Quantity("2048Mi");
        Quantity b = new Quantity("1Gi");

        assertThat(resourceService.gte(a, b)).isTrue();
      }

      @Test
      @DisplayName("A의 값이 B과 동일할 때는 true를 반환한다.")
      void equalToTest() throws Exception {
        Quantity a = new Quantity("1024Mi");
        Quantity b = new Quantity("1024Mi");

        assertThat(resourceService.gte(a, b)).isTrue();
      }

      @Test
      @DisplayName("A의 값이 B보다 작을 때는 false를 반환한다.")
      void lessThanTest() throws Exception {
        Quantity a = new Quantity("1Gi");
        Quantity b = new Quantity("2048Mi");

        assertThat(resourceService.gte(a, b)).isFalse();
      }

    }

    @Nested
    @EnableKubernetesMockClient(crud = true)
    @DisplayName("ResourceService.isDeployable(ResourceDto, ResourceDto) 메소드의 동작을 검증한다.")
    class isDeployableTest {

      @Test
      @DisplayName("desired에 정의된 모든 리소스를 allocatable에서 할당 가능할 경우 true를 반환한다.")
      void deployableTest() throws Exception {
        Resource allocatableEntity = Resource.builder()
            .cpu("2")
            .memory("4Gi")
            .gpu("3")
            .build();

        ResourceDto allocatable = resourceMapper.toServiceDto(allocatableEntity);

        ResourceDto desired = ResourceDto.builder()
            .cpu(new Quantity("500m"))
            .memory(new Quantity("1Gi"))
            .gpu(new Quantity("1"))
            .cpuLimit(new Quantity("500m"))
            .memoryLimit(new Quantity("1Gi"))
            .gpuLimit(new Quantity("1"))
            .build();

        assertThat(resourceService.isDeployable(allocatable, desired)).isTrue();
      }

      @Test
      @DisplayName("desired에 정의된 리소스 중 하나라도 allocatable에서 할당 불가능할 경우 false를 반환한다.")
      void notDeployableTest() throws Exception {
        Map<String, Quantity> allocatableMap = new HashMap<>();

        allocatableMap.put("cpu", new Quantity("2"));
        allocatableMap.put("memory", new Quantity("4Gi"));

        ResourceDto allocatable = resourceMapper.toServiceDto(allocatableMap);

        ResourceDto desired = ResourceDto.builder()
            .cpu(new Quantity("500m"))
            .memory(new Quantity("1Gi"))
            .gpu(new Quantity("1"))
            .build();

        assertThat(resourceService.isDeployable(allocatable, desired)).isFalse();
      }

    }

  }

}
