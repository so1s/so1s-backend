package io.so1s.backend.domain.auth.service;

import io.so1s.backend.domain.auth.entity.User;

public interface UserService {

  User createUser(String username, String password);
}
