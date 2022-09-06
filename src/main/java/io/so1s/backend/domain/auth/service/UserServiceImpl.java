package io.so1s.backend.domain.auth.service;

import io.so1s.backend.domain.auth.entity.User;
import io.so1s.backend.domain.auth.entity.UserToRole;
import io.so1s.backend.domain.auth.exception.DuplicateUserException;
import io.so1s.backend.domain.auth.exception.UnableToCreateUserException;
import io.so1s.backend.domain.auth.repository.UserRepository;
import io.so1s.backend.domain.auth.repository.UserToRoleRepository;
import io.so1s.backend.domain.auth.vo.UserRole;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final UserToRoleRepository userToRoleRepository;

  private final PasswordEncoder passwordEncoder;

  private String parseUserName(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof UserDetails) {
      UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
      return springSecurityUser.getUsername();
    } else if (principal instanceof String) {
      return (String) principal;
    }

    return null;
  }

  @Override
  @Transactional(rollbackFor = {DuplicateUserException.class})
  public User createUser(String username, String password, UserRole role)
      throws DuplicateUserException {
    Optional<User> previous = findByUsername(username);

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

  @Transactional(rollbackFor = {UnableToCreateUserException.class})
  public User signUp(String username, String password) throws UnableToCreateUserException {
    User currentUser = getCurrentUser();

    UserRole userRole = currentUser.getUserToRoles().stream().sorted(
            Comparator.comparingInt(a -> a.getUserRole()
                .getId()))
        .map(UserToRole::getUserRole)
        .limit(1)
        .collect(Collectors.toList())
        .get(0);

    List<UserRole> userRoles = List.of(UserRole.OWNER, UserRole.ADMIN, UserRole.USER);

    int userRoleIndex = userRoles.indexOf(userRole);
    int childRoleIndex = userRoleIndex + 1;

    if (childRoleIndex >= 3) {
      throw new UnableToCreateUserException("사용자는 계정 생성이 불가능합니다.");
    }

    UserRole childUserRole = userRoles.get(childRoleIndex);

    return createUser(username, password, childUserRole);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public Optional<String> getCurrentUsername() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      log.debug("Security Context에 인증 정보가 없습니다.");
      return Optional.empty();
    }

    String username = parseUserName(authentication);

    return Optional.ofNullable(username);
  }


  @Override
  public User getCurrentUser() {
    Optional<String> optionalCurrentUsername = getCurrentUsername();

    if (optionalCurrentUsername.isEmpty()) {
      return null;
    }

    String currentUsername = optionalCurrentUsername.get();

    Optional<User> optionalUser = findByUsername(currentUsername);

    if (optionalUser.isEmpty()) {
      return null;
    }

    return optionalUser.get();
  }
}
