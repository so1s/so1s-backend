package io.so1s.backend.global.initializer.common;

import io.so1s.backend.domain.auth.repository.UserRepository;
import io.so1s.backend.domain.auth.service.UserService;
import io.so1s.backend.domain.auth.vo.UserRole;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

  private final UserService userService;
  private final UserRepository userRepository;
  private static final String adminUsername = "so1s";
  private static final String adminPassword = "admin12345";

  @PostConstruct
  private void createAdmin() {
    userRepository.findByUsername(adminUsername).orElseGet(() ->
        userService.createUser(adminUsername, adminPassword, UserRole.ADMIN)
    );
  }

}
