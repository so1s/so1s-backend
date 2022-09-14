package io.so1s.backend.global.filter;

import io.so1s.backend.global.filter.wrapper.RequestWrapper;
import io.so1s.backend.global.filter.wrapper.ResponseWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

  private static void logRequest(RequestWrapper requestWrapper) throws IOException {
    log.info("[REQUEST] Method=[{}], url=[{}], Header=[{}], Body=[{}]",
        requestWrapper.getMethod(), requestWrapper.getRequestURI(),
        getRequestHeaders(requestWrapper),
        getPayload(requestWrapper.getContentType(), requestWrapper.getInputStream()));
  }

  private static Map getRequestHeaders(HttpServletRequest request) {
    Map headerMap = new HashMap<>();
    Enumeration headerArray = request.getHeaderNames();
    while (headerArray.hasMoreElements()) {
      String headerName = (String) headerArray.nextElement();
      headerMap.put(headerName, request.getHeader(headerName));
    }
    return headerMap;
  }

  private static void logResponse(ResponseWrapper responseWrapper) throws IOException {
    log.info("[RESPONSE] Status=[{}], Header=[{}], Body=[{}]",
        responseWrapper.getStatus(),
        getResponseHeader(responseWrapper),
        getPayload(responseWrapper.getContentType(), responseWrapper.getContentInputStream()));
  }

  private static Map getResponseHeader(HttpServletResponse response) {
    Map headerMap = new HashMap<>();
    Collection<String> headerNames = response.getHeaderNames();
    for (String headerName : headerNames) {
      headerMap.put(headerName, response.getHeader(headerName));
    }
    return headerMap;
  }

  private static String getPayload(String contentType, InputStream inputStream) throws IOException {
    if (isVisible(MediaType.valueOf(contentType == null ? "application/json" : contentType))) {
      byte[] content = StreamUtils.copyToByteArray(inputStream);
      if (content.length > 0) {
        return new String(content);
      }
    }
    return " - ";
  }

  private static boolean isVisible(MediaType mediaType) {
    final List<MediaType> VISIBLE_TYPES = Arrays.asList(
        MediaType.valueOf("text/*"),
        MediaType.APPLICATION_FORM_URLENCODED,
        MediaType.APPLICATION_JSON,
        MediaType.APPLICATION_XML,
        MediaType.valueOf("application/*+json"),
        MediaType.valueOf("application/*+xml"),
        MediaType.MULTIPART_FORM_DATA
    );

    return VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (isAsyncDispatch(request)) {
      filterChain.doFilter(request, response);
    } else {
      doFilterWrapped(new RequestWrapper(request), new ResponseWrapper(response), filterChain);
    }
  }

  protected void doFilterWrapped(RequestWrapper request, ResponseWrapper response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      logRequest(request);
      filterChain.doFilter(request, response);
    } finally {
      logResponse(response);
      response.copyBodyToResponse();
    }
  }
}
