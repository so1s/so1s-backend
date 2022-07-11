package io.so1s.backend.global.domain.auth.controller;

import io.so1s.backend.global.domain.auth.dto.request.LoginRequestDto;
import io.so1s.backend.global.domain.auth.dto.response.TokenResponseDto;
import io.so1s.backend.global.domain.auth.security.filter.JwtFilter;
import io.so1s.backend.global.domain.auth.security.provider.TokenProvider;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/signin")
@RequiredArgsConstructor
public class SignInController {

  private final TokenProvider tokenProvider;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  @PostMapping
  public ResponseEntity<TokenResponseDto> authorize(@Valid @RequestBody LoginRequestDto loginDto) {

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

    Authentication authentication = authenticationManagerBuilder.getObject()
        .authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = tokenProvider.createToken(authentication);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(JwtFilter.getAUTHORIZATION_HEADER(), JwtFilter.getHEADER_PREFIX() + jwt);

    TokenResponseDto responseDto = new TokenResponseDto(jwt);

    return ResponseEntity.status(HttpStatus.OK)
        .headers(httpHeaders)
        .body(responseDto);
  }
}