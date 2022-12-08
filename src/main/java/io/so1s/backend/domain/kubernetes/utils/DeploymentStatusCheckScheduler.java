package io.so1s.backend.domain.kubernetes.utils;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.kubernetes.service.NamespaceService;
import io.so1s.backend.global.vo.Status;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j
public class DeploymentStatusCheckScheduler {

  private final KubernetesClient client;
  private final DeploymentRepository deploymentRepository;
  private final ApplicationHealthChecker applicationHealthChecker;
  private final NamespaceService namespaceService;

  @Autowired
  @Lazy
  private DeploymentStatusCheckScheduler self;

  @Scheduled(fixedDelay = 1000L * 60)
  public void checkDeploymentStatus() {
    log.info("Scheduled method DeploymentStatusCheckScheduler.checkDeploymentStatus() invoked");

    List<Deployment> deployments = deploymentRepository.findAll();

    List<io.fabric8.kubernetes.api.model.apps.Deployment> k8sDeployments = client.apps()
        .deployments().inAnyNamespace().withLabel("app", "inference")
        .list().getItems();

    deployments.stream().parallel().forEach(deployment -> {
      Optional<io.fabric8.kubernetes.api.model.apps.Deployment> find = k8sDeployments.stream()
          .parallel().filter(d -> d.getMetadata().getName().equalsIgnoreCase(
              String.format("inference-%s", deployment.getName())))
          .findAny();
      find.ifPresentOrElse(e -> {
        if (e.getStatus().getConditions().stream()
            .anyMatch(cond -> cond.getStatus().equals("True"))) {
          self.setDeploymentStatus(deployment, Status.RUNNING);
        } else {
          self.setDeploymentStatus(deployment, Status.FAILED);
        }
      }, () -> self.setDeploymentStatus(deployment, Status.UNKNOWN));
    });
  }

  @Transactional
  public void setDeploymentStatus(Deployment deployment, Status status) {
    log.info(String.format(
        "DeploymentStatusCheckScheduler.setDeploymentStatus change deployment %s status to %s",
        deployment, status));
    deployment.changeStatus(status);
    deploymentRepository.save(deployment);
  }
}
