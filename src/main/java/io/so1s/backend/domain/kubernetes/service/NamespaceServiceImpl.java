package io.so1s.backend.domain.kubernetes.service;

import io.so1s.backend.domain.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NamespaceServiceImpl implements NamespaceService {

  private final UserService userService;

  public String getNamespace() {
    return "so1s-" + userService.getCurrentUsername();
  }

}
