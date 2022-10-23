package io.so1s.backend.unit.resource.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import io.fabric8.istio.mock.EnableIstioMockClient;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Taint;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.so1s.backend.domain.kubernetes.service.NodesService;
import io.so1s.backend.domain.resource.dto.mapper.ResourceMapper;
import io.so1s.backend.domain.resource.dto.service.ResourceDto;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.service.ResourceService;
import io.so1s.backend.global.utils.HashGenerator;
import io.so1s.backend.unit.kubernetes.config.TestKubernetesConfig;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {TestKubernetesConfig.class})
@EnableKubernetesMockClient(crud = true)
@EnableIstioMockClient(crud = true)
@ExtendWith(MockitoExtension.class)
@WithMockUser
@ActiveProfiles(profiles = {"test"})
@DisplayName("ResourceService에서")
public class ResourceServiceTest {

  @Autowired
  private ResourceService resourceService;
  @Autowired
  private ResourceMapper resourceMapper;
  @SpyBean
  private NodesService nodesService;

  @Nested
  @EnableKubernetesMockClient(crud = true)
  @EnableIstioMockClient(crud = true)
  @DisplayName("Node들의 리소스로부터 Deployment 리소스가 할당 가능한지 확인한다.")
  class NodeResourceDeployableTest {

    @EnableKubernetesMockClient(crud = true)
    @EnableIstioMockClient(crud = true)
    @Nested
    @DisplayName("ResourceService.gte(Quantity, Quantity) 메소드는")
    class GteTest {

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
    @EnableIstioMockClient(crud = true)
    @DisplayName("ResourceService.isDeployable(ResourceDto, ResourceDto) 메소드는")
    class IsDeployableTest {

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
        allocatableMap.put("nvidia.com/gpu", new Quantity("0"));

        Map<String, Quantity> desiredMap = new HashMap<>();

        desiredMap.put("cpu", new Quantity("500m"));
        desiredMap.put("memory", new Quantity("1Gi"));
        desiredMap.put("nvidia.com/gpu", new Quantity("1"));

        ResourceDto allocatable = resourceMapper.toServiceDto(allocatableMap);
        ResourceDto desired = resourceMapper.toServiceDto(desiredMap);

        assertThat(resourceService.isDeployable(allocatable, desired)).isFalse();
      }

      @Test
      @DisplayName("CPU 1개를 500m보다 큰 단위로 인식한다.")
      void checkQuantityUnits() throws Exception {
        Map<String, Quantity> allocatableMap = new HashMap<>();

        allocatableMap.put("cpu", new Quantity("1"));
        allocatableMap.put("memory", new Quantity("2Gi"));
        allocatableMap.put("nvidia.com/gpu", new Quantity("0"));

        Map<String, Quantity> desiredMap = new HashMap<>();

        desiredMap.put("cpu", new Quantity("500m"));
        desiredMap.put("memory", new Quantity("1Gi"));
        desiredMap.put("nvidia.com/gpu", new Quantity("0"));

        ResourceDto allocatable = resourceMapper.toServiceDto(allocatableMap);
        ResourceDto desired = resourceMapper.toServiceDto(desiredMap);

        assertThat(resourceService.isDeployable(allocatable, desired)).isTrue();
      }

    }

    @Nested
    @EnableKubernetesMockClient(crud = true)
    @EnableIstioMockClient(crud = true)
    @DisplayName("ResourceService.isDeployable(Resource) 메소드는")
    class NodesDeployableTest {

      final Resource resource = Resource.builder()
          .cpu("1")
          .memory("1Gi")
          .gpu("1")
          .build();

      Node createExampleNode(String cpu, String memory, String gpu, String taint) {
        Map<String, Quantity> allocatable = Map.ofEntries(
            Map.entry("cpu", new Quantity(cpu)),
            Map.entry("memory", new Quantity(memory)),
            Map.entry("nvidia.com/gpu", new Quantity(gpu))
        );
        Map<String, Quantity> capacity = new HashMap<>(allocatable);

        Taint inferenceTaint = new Taint("NoSchedule", "kind", LocalDate.now().toString(), taint);

        return new NodeBuilder()
            .withNewMetadata()
            .withName(HashGenerator.sha256())
            .endMetadata()
            .withNewSpec()
            .addNewTaintLike(inferenceTaint)
            .endTaint()
            .endSpec()
            .withNewStatus()
            .withAllocatable(allocatable)
            .withCapacity(capacity)
            .endStatus()
            .build();
      }

      @Test
      @DisplayName("아무 노드도 없는 환경에서는 할당을 원하는 리소스를 제공할 수 없다.")
      void noNodeTest() throws Exception {
        List<Node> nodes = List.of();

        given(nodesService.findNodes()).willReturn(nodes);

        assertThat(resourceService.isDeployable(resource)).isFalse();
      }

      @Test
      @DisplayName("인퍼런스 Taint가 존재하지 않는 노드들만 존재하는 환경에서는 할당을 원하는 리소스를 제공할 수 없다.")
      void noInferenceNodeTest() throws Exception {
        List<Node> nodes = List.of(
            createExampleNode("4", "4Gi", "3", "application")
        );

        given(nodesService.findNodes()).willReturn(nodes);

        assertThat(resourceService.isDeployable(resource)).isFalse();
      }

      @Test
      @DisplayName("현재 노드가 할당 가능한 리소스를 초과해서 제공할 수 없다.")
      void resourceExceededTest() throws Exception {
        List<Node> nodes = List.of(
            createExampleNode("500m", "512Mi", "1", "inference")
        );

        given(nodesService.findNodes()).willReturn(nodes);

        assertThat(resourceService.isDeployable(resource)).isFalse();
      }

      @Test
      @DisplayName("현재 노드가 할당 가능한 리소스일 경우 제공할 수 있다.")
      void singleNodeTest() throws Exception {
        List<Node> nodes = List.of(
            createExampleNode("4", "4Gi", "3", "inference")
        );

        given(nodesService.findNodes()).willReturn(nodes);

        assertThat(resourceService.isDeployable(resource)).isTrue();
      }

      @Test
      @DisplayName("일부 노드에서 할당이 불가능하고, 다른 일부는 할당 가능한 리소스일 경우 제공할 수 있다.")
      void partialApplicationTest() throws Exception {
        List<Node> nodes = List.of(
            createExampleNode("4", "4Gi", "3", "inference"),
            createExampleNode("500m", "512Mi", "1", "inference")
        );

        given(nodesService.findNodes()).willReturn(nodes);

        assertThat(resourceService.isDeployable(resource)).isTrue();

      }

    }

  }

}
