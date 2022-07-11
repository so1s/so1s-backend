package io.so1s.backend.domain.auth.service;

import io.so1s.backend.domain.auth.entity.User;
import io.so1s.backend.domain.auth.entity.UserToRole;
import io.so1s.backend.domain.auth.repository.UserRepository;
import io.so1s.backend.domain.auth.repository.UserToRoleRepository;
import io.so1s.backend.domain.auth.vo.UserRole;
import io.so1s.backend.global.error.exception.DuplicateUserException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final UserToRoleRepository userToRoleRepository;

  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public User createUser(String username, String password, UserRole role) {
    Optional<User> previous = userRepository.findByUsername(username);

    if (previous.isPresent()) {
      throw new DuplicateUserException("이미 존재하는 사용자입니다.");
    }

    User user = userRepository.save(
        User.builder().username(username).password(passwordEncoder.encode(password)).build());

    List<UserRole> userRoles = List.of(UserRole.USER, UserRole.ADMIN, UserRole.OWNER);

    int roleIndex = userRoles.indexOf(role);

    List<UserToRole> userToRoles = userRoles.stream()
        .filter(e -> userRoles.indexOf(e) <= roleIndex)
        .map(userRole -> UserToRole.builder().user(user).userRole(userRole).build())
        .peek(userToRoleRepository::save)
        .collect(Collectors.toList());

    user.addAllUserToRole(userToRoles);

    return user;
  }
}
