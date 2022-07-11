package io.so1s.backend.global.domain.auth.config;

import io.so1s.backend.global.domain.auth.security.filter.JwtFilter;
import io.so1s.backend.global.domain.auth.security.provider.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtFilterConfig {

  private final TokenProvider tokenProvider;

  @Bean
  public JwtFilter jwtFilter() {
    return new JwtFilter(tokenProvider);
  }

}
