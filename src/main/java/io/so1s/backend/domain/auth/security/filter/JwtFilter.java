package io.so1s.backend.domain.auth.security.filter;

import io.so1s.backend.domain.auth.security.provider.TokenProvider;
import io.so1s.backend.global.error.ErrorResponseDto;
import io.so1s.backend.global.utils.JsonMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  @Getter
  private static final String AUTHORIZATION_HEADER = "Authorization";
  @Getter
  private static final String HEADER_PREFIX = "Bearer ";

  private final TokenProvider tokenProvider;
  private final JsonMapper jsonMapper;


  @Override
  protected void doFilterInternal(HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      FilterChain filterChain)
      throws ServletException, IOException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    String jwt = resolveToken(httpServletRequest);
    String requestURI = httpServletRequest.getRequestURI();

    if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
      Authentication authentication = tokenProvider.getAuthentication(jwt);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(),
          requestURI);
    } else {
      log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
    }

    try {
      filterChain.doFilter(servletRequest, servletResponse);
    } catch (Exception ex) {
      log.info(String.format("Exception filter %s", ex.getMessage()));
      setErrorResponse(HttpStatus.BAD_REQUEST, (HttpServletResponse) servletResponse, ex);
    }
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) {
    response.setStatus(status.value());
    response.setContentType("application/json");
    ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().message(ex.getMessage()).build();
    try {
      String result = jsonMapper.asJsonString(errorResponseDto);
      log.info("!!" + result);
      response.getWriter().write(result);
    } catch (IOException ignored) {
      log.error("Failed to set error response because of IOException");
    }
  }
}
