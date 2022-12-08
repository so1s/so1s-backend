package io.so1s.backend.domain.kubernetes.utils;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.global.vo.Status;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobStatusChecker {

  private final KubernetesClient client;

  @Async
  public void checkJobStatus(String jobName, String namespace, ModelMetadata modelMetadata)
      throws InterruptedException {
    checkJobStatusSync(jobName, namespace, modelMetadata);
  }

  public void checkJobStatusSync(String jobName, String namespace, ModelMetadata modelMetadata)
      throws InterruptedException {
    long interval = 10;

    Map<String, String> labels = new HashMap<>();
    labels.put("app", "inference-build");
    labels.put("name", jobName);

    while (true) {
      Job job = client.batch().v1().jobs().inNamespace(namespace).withLabels(labels).list()
          .getItems().get(0);

      if (job.getStatus().getActive() == null) {
        if (job.getStatus().getSucceeded() != null && job.getStatus().getSucceeded() == 1) {
          modelMetadata.changeStatus(Status.SUCCEEDED);
          break;
        } else if (job.getStatus().getFailed() != null && job.getStatus().getFailed() == 1) {
          modelMetadata.changeStatus(Status.FAILED);
          break;
        }
      }

      Thread.sleep(interval * 1000);
    }
  }
}
