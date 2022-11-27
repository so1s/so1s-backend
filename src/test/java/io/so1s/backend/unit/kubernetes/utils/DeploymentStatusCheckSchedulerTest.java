package io.so1s.backend.unit.kubernetes.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.fabric8.kubernetes.api.model.TolerationBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentCondition;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.so1s.backend.domain.deployment.dto.request.Standard;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.deployment_strategy.repository.DeploymentStrategyRepository;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.kubernetes.utils.ApplicationHealthChecker;
import io.so1s.backend.domain.kubernetes.utils.DeploymentStatusCheckScheduler;
import io.so1s.backend.domain.library.entity.Library;
import io.so1s.backend.domain.library.repository.LibraryRepository;
import io.so1s.backend.domain.model.entity.Model;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.domain.model.repository.ModelRepository;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.resource.repository.ResourceRepository;
import io.so1s.backend.global.config.JpaConfig;
import io.so1s.backend.global.utils.HashGenerator;
import io.so1s.backend.global.vo.Status;
import io.so1s.backend.unit.kubernetes.config.TestKubernetesConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { TestKubernetesConfig.class })
@Import(JpaConfig.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser
@ActiveProfiles(profiles = { "test" })
public class DeploymentStatusCheckSchedulerTest {

    @Autowired
    KubernetesClient client;

    @Autowired
    DeploymentStatusCheckScheduler deploymentStatusCheckScheduler;

    @Autowired
    KubernetesService kubernetesService;

    @Autowired
    ResourceRepository resourceRepository;
    @Autowired
    DeploymentStrategyRepository deploymentStrategyRepository;
    @Autowired
    DeploymentRepository deploymentRepository;
    @Autowired
    ModelMetadataRepository modelMetadataRepository;
    @Autowired
    LibraryRepository libraryRepository;
    @Autowired
    ModelRepository modelRepository;

    @MockBean
    ApplicationHealthChecker applicationHealthChecker;

    @Test
    @DisplayName("디플로이먼트의 상태를 성공적으로 감지하면 RUNNING으로 변경한다.")
    public void schdulingTest() throws Exception {
        // given
        Library lib = libraryRepository.findByName("tensorflow").get();
        Model model = modelRepository.save(Model.builder()
                .name("testModel")
                .library(lib)
                .build());
        ModelMetadata modelMetadata = modelMetadataRepository.save(ModelMetadata.builder()
                .status(Status.SUCCEEDED)
                .version(HashGenerator.sha256())
                .fileName("testModelFile")
                .url("https://s3.testModelFile.com")
                .inputShape("(10,)")
                .inputDtype("float32")
                .outputShape("(1,)")
                .outputDtype("float32")
                .deviceType("cpu")
                .model(model)
                .build());
        DeploymentStrategy deploymentStrategy = deploymentStrategyRepository.findByName("rolling")
                .get();
        Resource resource = resourceRepository.save(Resource.builder()
                .name("schdulingTest")
                .cpu("1")
                .memory("1Gi")
                .gpu("0")
                .cpuLimit("2")
                .memoryLimit("2Gi")
                .gpuLimit("0")
                .build());
        Deployment deployment = deploymentRepository.save(Deployment.builder()
                .name("test-deployment")
                .endPoint("www.test.io")
                .status(Status.PENDING)
                .standard(Standard.REPLICAS)
                .standardValue(1)
                .modelMetadata(modelMetadata)
                .deploymentStrategy(deploymentStrategy)
                .resource(resource)
                .build());

        String namespace = kubernetesService.getNamespace();
        String deployName = deployment.getName().toLowerCase();
        Map<String, String> labels = new HashMap<>();
        labels.put("app", "inference");
        labels.put("name", deployName);

        io.fabric8.kubernetes.api.model.apps.Deployment inferenceDeployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName(deployName)
                .withNamespace(namespace)
                .addToLabels(labels)
                .endMetadata()
                .withNewSpec()
                .withReplicas(1)
                .withNewSelector()
                .addToMatchLabels(labels)
                .endSelector()
                .withNewTemplate()
                .withNewMetadata()
                .withName(deployName)
                .addToLabels(labels)
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withImagePullPolicy("Always")
                .withName(deployName)
                .withImage("so1s/" + model.getName() + ":" + modelMetadata.getVersion())
                .addNewPort()
                .withName("inference-port")
                .withContainerPort(3000)
                .endPort()
                .endContainer()
                .withTolerations(new TolerationBuilder()
                        .withKey("kind")
                        .withOperator("Equal")
                        .withValue("inference")
                        .withEffect("NoSchedule")
                        .build())
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();

        DeploymentCondition deploymentCondition = new DeploymentCondition();
        deploymentCondition.setStatus("True");
        DeploymentStatus deploymentStatus = new DeploymentStatus();
        deploymentStatus.getConditions().add(deploymentCondition);
        inferenceDeployment.setStatus(deploymentStatus);

        client.apps().deployments().inNamespace(namespace).createOrReplace(inferenceDeployment);

        when(applicationHealthChecker.checkApplicationHealth(anyString())).thenReturn(true);

        // when
        deploymentStatusCheckScheduler.checkDeploymentStatus();

        // then
        Optional<Deployment> findDeployment = deploymentRepository.findById(deployment.getId());
        findDeployment.ifPresent(value -> assertThat(value.getStatus()).isEqualTo(Status.RUNNING));
    }
}
