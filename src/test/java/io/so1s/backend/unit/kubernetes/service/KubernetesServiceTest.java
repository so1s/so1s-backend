package io.so1s.backend.unit.kubernetes.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import io.fabric8.istio.client.IstioClient;
import io.fabric8.istio.mock.EnableIstioMockClient;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeBuilder;
import io.fabric8.kubernetes.api.model.autoscaling.v2beta2.HorizontalPodAutoscalerList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.so1s.backend.domain.deployment.dto.request.Standard;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.kubernetes.service.KubernetesServiceImpl;
import io.so1s.backend.domain.kubernetes.utils.JobStatusChecker;
import io.so1s.backend.domain.library.entity.Library;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.test.entity.ABTest;
import io.so1s.backend.global.utils.HashGenerator;
import io.so1s.backend.global.vo.Status;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@EnableKubernetesMockClient(crud = true)
@EnableIstioMockClient(crud = true)
@ExtendWith(MockitoExtension.class)
@WithMockUser
@ActiveProfiles(profiles = {"test"})
public class KubernetesServiceTest {

  KubernetesService kubernetesService;
  KubernetesClient client;
  IstioClient istioClient;

  JobStatusChecker jobStatusChecker;

  @BeforeEach
  public void setup() {
    jobStatusChecker = Mockito.mock(JobStatusChecker.class);
    kubernetesService = new KubernetesServiceImpl(client, istioClient, jobStatusChecker);
  }

  @Test
  @DisplayName("성공적으로 인퍼런스 잡이 실행되면 true를 반환한다.")
  public void inferenceServerBuild() throws Exception {
    // given
    Model model = getModel("FinetunedModel");
    ModelMetadata modelMetadata = getModelMetadata(model);
    doNothing().when(jobStatusChecker).checkJobStatus(any(), any(), any());

    // when
    boolean result = kubernetesService.inferenceServerBuild(modelMetadata);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("성공적으로 네임스페이스가 생성되면 true를 반환한다.")
  public void createNamespaceTest() throws Exception {
    // given
    String namespace = "test-space";

    // when
    boolean result = kubernetesService.createNamespace(namespace);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("성공적으로 리소스 쿼타가 생성되면 true를 반환한다.")
  public void createResourceQuotaTest() throws Exception {
    // given
    Resource resource = getResource(false);
    String namespace = "test-space";

    // when
    boolean result = kubernetesService.createResourceQuota(resource, namespace);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("성공적으로 리소스 쿼타(withGpu)가 생성되면 true를 반환한다.")
  public void createResourceQuotaWithGpuTest() throws Exception {
    // given
    Resource resource = getResource(true);
    String namespace = "test-space";

    // when
    boolean result = kubernetesService.createResourceQuotaWithGpu(resource, namespace);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("성공적으로 인퍼런스 서버를 생성하면 true를 반환한다. 작업이 끝나면 삭제한다.")
  public void deployInferenceServerTest() throws Exception {
    // given
    Model model = getModel("testModel");
    ModelMetadata modelMetadata = getModelMetadata(model);
    Resource resource = getResource(false);
    Deployment deployment = getDeployment("testDeployment", modelMetadata, resource);

    addNewNodeForTolerations();

    ExecutorService executor
        = Executors.newSingleThreadExecutor();

    // when
    boolean result = kubernetesService.deployInferenceServer(deployment);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("성공적으로 AB 테스트를 생성하면 true를 반환한다.")
  public void deployABTest() throws Exception {
    // given
    Deployment a = getDeployment("a", getModelMetadata(getModel("bModel")), getResource(false));
    Deployment b = getDeployment("b", getModelMetadata(getModel("bModel")), getResource(false));

    ABTest abTest = ABTest.builder()
        .name("abTest")
        .a(a)
        .b(b)
        .domain("so1s.io")
        .build();

    addNewNodeForTolerations();

    ExecutorService executor
        = Executors.newSingleThreadExecutor();

    // when

    boolean result = kubernetesService.deployInferenceServer(a);
    result = result && kubernetesService.deployInferenceServer(b);
    result = result && kubernetesService.deployABTest(abTest);

    // then

    assertThat(result).isTrue();

    // Clean up

    // when

    result = kubernetesService.deleteABTest(abTest);
    result = result && kubernetesService.deleteDeployment(a);
    result = result && kubernetesService.deleteDeployment(b);

    // then

    assertThat(result).isTrue();
  }

  public void addNewNodeForTolerations() {
    Map<String, String> labels = new HashMap<>();

    labels.put("kind", "inference");

    Node node = new NodeBuilder()
        .withNewMetadata()
        .withName("TestNode")
        .withLabels(labels)
        .endMetadata()
        .withNewSpec()
        .endSpec()
        .build();

    client.nodes().create(node);
  }

  @Test
  @DisplayName("성공적으로 HPA true를 반환한다.")
  public void createHPATest() throws Exception {
    // given
    Model model = getModel("testModel");
    ModelMetadata modelMetadata = getModelMetadata(model);
    Resource resource = getResource(false);
    Deployment deployment = Deployment.builder()
        .name("testDeployment")
        .status(Status.PENDING)
        .endPoint("inference-" + "testDeployment".toLowerCase() + ".so1s.io")
        .standard(Standard.LATENCY)
        .standardValue(20)
        .maxReplicas(10)
        .minReplicas(1)
        .modelMetadata(modelMetadata)
        .resource(resource)
        .build();

    addNewNodeForTolerations();

    // when
    boolean result = kubernetesService.createHPA(deployment, "default");

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("HPA 설정 없이 인퍼런스를 생성 할 수 있다.")
  public void deployInferenceServerWithoutHPATest() throws Exception {
    // given
    Model model = getModel("testModel");
    ModelMetadata modelMetadata = getModelMetadata(model);
    Resource resource = getResource(false);
    Deployment deployment = Deployment.builder()
        .name("testDeployment")
        .status(Status.PENDING)
        .endPoint("inference-" + "testDeployment".toLowerCase() + ".so1s.io")
        .standard(Standard.REPLICAS)
        .standardValue(5)
        .modelMetadata(modelMetadata)
        .resource(resource)
        .build();

    addNewNodeForTolerations();

    // when
    boolean result = kubernetesService.deployInferenceServer(deployment);
    List<io.fabric8.kubernetes.api.model.apps.Deployment> deployments = client.apps().deployments()
        .inNamespace("default")
        .withLabel("app", "inference").list()
        .getItems();

    io.fabric8.kubernetes.api.model.apps.Deployment createdDeployment = (io.fabric8.kubernetes.api.model.apps.Deployment) kubernetesService.getDeploymentObject(
        "testDeployment".toLowerCase());

    HorizontalPodAutoscalerList hpaList = client.autoscaling().v2beta2().horizontalPodAutoscalers()
        .inNamespace("default").list();

    // then
    assertThat(result).isTrue();
    assertThat(createdDeployment.getSpec().getReplicas()).isEqualTo(5);
    assertThat(hpaList.getItems().size()).isEqualTo(0);

  }

  public Model getModel(String name) {
    return Model.builder()
        .name(name)
        .library(Library.builder()
            .name("torch")
            .build())
        .build();
  }

  public ModelMetadata getModelMetadata(Model model) {
    return ModelMetadata.builder()
        .status(Status.SUCCEEDED)
        .version(HashGenerator.sha256())
        .fileName("titanic.h5")
        .url("https://s3.test.com/")
        .inputShape("(10,)")
        .inputDtype("float32")
        .outputShape("(1,)")
        .outputDtype("float32")
        .deviceType("cpu")
        .model(model)
        .build();
  }

  public Resource getResource(boolean isGPU) {
    return Resource.builder()
        .name(HashGenerator.sha256())
        .cpu("1")
        .memory("1Gi")
        .gpu(isGPU ? "1" : "0")
        .cpuLimit("2")
        .memoryLimit("2Gi")
        .gpuLimit(isGPU ? "1" : "0")
        .build();
  }

  public Deployment getDeployment(String name, ModelMetadata modelMetadata, Resource resource) {
    return Deployment.builder()
        .name(name)
        .status(Status.PENDING)
        .endPoint("inference-" + name.toLowerCase() + ".so1s.io")
        .standard(Standard.REPLICAS)
        .standardValue(5)
        .modelMetadata(modelMetadata)
        .resource(resource)
        .build();
  }
}
