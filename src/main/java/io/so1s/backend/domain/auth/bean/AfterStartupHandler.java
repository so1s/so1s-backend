package io.so1s.backend.domain.auth.bean;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.so1s.backend.domain.auth.exception.DuplicatedUserException;
import io.so1s.backend.domain.auth.service.UserService;
import io.so1s.backend.domain.auth.vo.UserRole;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import java.net.UnknownHostException;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AfterStartupHandler {

  private final UserService userService;
  private final KubernetesService kubernetesService;
  private final KubernetesClient client;

  @PostConstruct
  public void init() {
    try {
      userService.createUser("so1s", "admin12345", UserRole.ADMIN);
      if (isConnected()) {
        kubernetesService.createNamespace("so1s-so1s");
      }
    } catch (DuplicatedUserException ignored) {
    }
  }

  private boolean isConnected() {
    try {
      client.getVersion();
    } catch (KubernetesClientException e) {
      if (e.getCause() instanceof UnknownHostException) {
        return false;
      }
    }
    return true;
  }
}