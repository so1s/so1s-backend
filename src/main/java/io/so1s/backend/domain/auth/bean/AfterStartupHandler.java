package io.so1s.backend.domain.auth.bean;

import io.so1s.backend.domain.auth.exception.DuplicatedUserException;
import io.so1s.backend.domain.auth.service.UserService;
import io.so1s.backend.domain.auth.vo.UserRole;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AfterStartupHandler {

  private final UserService userService;
//  private final KubernetesService kubernetesService;

  @PostConstruct
  public void init() {
    try {
      userService.createUser("so1s", "admin12345", UserRole.ADMIN);
//      kubernetesService.createNamespace("so1s");
    } catch (DuplicatedUserException ignored) {
    }
  }
}