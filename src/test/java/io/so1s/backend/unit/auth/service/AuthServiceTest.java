package io.so1s.backend.unit.auth.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.so1s.backend.domain.auth.exception.DuplicatedUserException;
import io.so1s.backend.domain.auth.service.UserService;
import io.so1s.backend.domain.auth.vo.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(SpringExtension.class)
@SpringBootTest
// Flush DB after each test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// For use test jwt secret
@ActiveProfiles(profiles = {"test"})
class AuthServiceTest {

  @Autowired
  private UserService userService;

  @Test
  @DisplayName("사용자명이 중복되는 사용자를 회원가입하려는 경우 익셉션이 발생한다.")
  void testDuplicateUserCreation() {
    String username = "so1s";
    String password = "so1s";

    userService.createUser("so1s", "so1s", UserRole.USER);

    assertThrows(DuplicatedUserException.class, () -> {
      userService.createUser("so1s", "so1s", UserRole.USER);
    });

  }

}
