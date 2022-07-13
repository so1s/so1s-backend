package io.so1s.backend.domain.auth.service;

import io.so1s.backend.domain.auth.entity.User;
import io.so1s.backend.domain.auth.vo.UserRole;
import java.util.Optional;

public interface UserService {

  User createUser(String username, String password, UserRole role);

  User signUp(String username, String password);

  Optional<User> findByUsername(String username);

  Optional<String> getCurrentUsername();

  User getCurrentUser();

}
