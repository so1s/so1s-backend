package io.so1s.backend.domain.auth.service;

import io.so1s.backend.domain.auth.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserService userService;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(final String username) {
    return userService.findByUsername(username)
        .map(this::getUser)
        .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
  }

  private org.springframework.security.core.userdetails.User getUser(
      User user) {
    List<GrantedAuthority> grantedAuthorities = user.getUserToRoles().stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getUserRole().toString()))
        .collect(Collectors.toList());
    return new org.springframework.security.core.userdetails.User(user.getUsername(),
        user.getPassword(),
        grantedAuthorities);
  }
}
