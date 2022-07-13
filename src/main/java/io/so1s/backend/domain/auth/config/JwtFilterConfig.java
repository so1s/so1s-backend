package io.so1s.backend.domain.auth.config;

import io.so1s.backend.domain.auth.security.filter.JwtFilter;
import io.so1s.backend.domain.auth.security.provider.TokenProvider;
import io.so1s.backend.global.utils.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtFilterConfig {

  private final TokenProvider tokenProvider;

  private final JsonMapper jsonMapper;

  @Bean
  public JwtFilter jwtFilter() {
    return new JwtFilter(tokenProvider, jsonMapper);
  }

}
