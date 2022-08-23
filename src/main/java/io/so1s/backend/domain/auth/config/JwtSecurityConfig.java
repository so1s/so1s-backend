package io.so1s.backend.domain.auth.config;

import io.so1s.backend.domain.auth.security.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtSecurityConfig extends
    SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

  private final JwtFilter jwtFilter;

  @Override
  public void configure(HttpSecurity http) {
    http.addFilterBefore(jwtFilter, SessionManagementFilter.class);
  }
}
