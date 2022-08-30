package io.so1s.backend.domain.kubernetes.utils;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.model.repository.ModelMetadataRepository;
import io.so1s.backend.global.entity.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobStatusChecker {

  private final KubernetesClient client;
  private final ModelMetadataRepository modelMetadataRepository;

  @Async
  public void checkJobStatus(String jobName, String namespace, ModelMetadata modelMetadata)
      throws InterruptedException {
    int interval = 10;

    while (true) {
      Job job = client.batch().v1().jobs().inNamespace(namespace).withLabel("job-name", jobName)
          .list().getItems().get(0);

      if (job.getStatus().getActive() == null) {
        if (job.getStatus().getSucceeded() != null && job.getStatus().getSucceeded() == 1) {
          changeModelMetadataStatus(Status.SUCCEEDED, modelMetadata);
          break;
        } else if (job.getStatus().getFailed() != null && job.getStatus().getFailed() == 1) {
          changeModelMetadataStatus(Status.FAILED, modelMetadata);
          break;
        }
      }

//      // 만약 ImagePullBackOff에러가 나는 경우가 발생한다면 필요한 구문
//      else {
//        Optional<ContainerStateWaiting> waiting = Optional.ofNullable(
//            client.pods().inNamespace(namespace).withLabel("job-name", jobName).list().getItems()
//                .get(0).getStatus().getContainerStatuses().get(0).getState().getWaiting());
//
//        if (waiting.isPresent()) {
//          if (waiting.get().getReason().equals("ImagePullBackOff")) {
//            changeModelMetadataStatus(Status.FAILED, modelMetadata);
//            break;
//          }
//        }
//      }

      Thread.sleep(interval * 1000);
    }
  }

  public void changeModelMetadataStatus(Status status, ModelMetadata modelMetadata) {
    modelMetadata.changeStatus(status);
    modelMetadataRepository.save(modelMetadata);
  }
}
