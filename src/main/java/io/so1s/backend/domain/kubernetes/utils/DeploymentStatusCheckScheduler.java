package io.so1s.backend.domain.kubernetes.utils;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.global.vo.Status;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class DeploymentStatusCheckScheduler {

  private final KubernetesClient client;
  private final DeploymentRepository deploymentRepository;
  private final ApplicationHealthChecker applicationHealthChecker;

  //  @Scheduled(fixedDelay = 1000L * 60)
  public void checkDeploymentStatus() {
    List<Deployment> deployments = deploymentRepository.findAll();

    List<io.fabric8.kubernetes.api.model.apps.Deployment> k8sDeployments = client.apps()
        .deployments().inNamespace("default").withLabel("app", "inference").list().getItems();

    for (Deployment deployment : deployments) {
      Optional<io.fabric8.kubernetes.api.model.apps.Deployment> find = k8sDeployments.stream()
          .parallel().filter(d -> d.getMetadata().getName().equalsIgnoreCase(deployment.getName()))
          .findAny();
      if (find.isPresent()) {
        if (find.get().getStatus().getConditions().get(0).getStatus().equals("True")) {
          if (applicationHealthChecker.checkApplicationHealth(deployment.getEndPoint())) {
            setDeploymentStatus(deployment, Status.RUNNING);
            continue;
          }
        }
        setDeploymentStatus(deployment, Status.FAILED);
      } else {
        setDeploymentStatus(deployment, Status.UNKNOWN);
      }
    }
  }

  @Transactional
  public void setDeploymentStatus(Deployment deployment, Status status) {
    deployment.changeStatus(status);
    deploymentRepository.save(deployment);
  }
}
