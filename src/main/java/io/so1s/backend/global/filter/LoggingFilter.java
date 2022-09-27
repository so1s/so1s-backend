package io.so1s.backend.global.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

  private static void logRequest(ContentCachingRequestWrapper requestWrapper) throws IOException {
    log.info("[REQUEST] Method=[{}], url=[{}], Header=[{}], Body=[{}]",
        requestWrapper.getMethod(), requestWrapper.getRequestURI(),
        getRequestHeaders(requestWrapper), getRequestPayload(requestWrapper));
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

  private static String getRequestPayload(ContentCachingRequestWrapper requestWrapper)
      throws IOException {
    ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(requestWrapper,
        ContentCachingRequestWrapper.class);
    if (wrapper != null) {
      byte[] buf = wrapper.getContentAsByteArray();
      if (buf.length > 0) {
        try {
          return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
          return " - ";
        }
      }
    }
    return " - ";
  }

  private static void logResponse(ContentCachingResponseWrapper responseWrapper) {
    log.info("[RESPONSE] Status=[{}], Header=[{}], Body=[{}]",
        responseWrapper.getStatus(),
        getResponseHeader(responseWrapper), getResponsePayload(responseWrapper));
  }

  private static Map getResponseHeader(HttpServletResponse response) {
    Map headerMap = new HashMap<>();
    Collection<String> headerNames = response.getHeaderNames();
    for (String headerName : headerNames) {
      headerMap.put(headerName, response.getHeader(headerName));
    }
    return headerMap;
  }

  private static String getResponsePayload(ContentCachingResponseWrapper responseWrapper) {
    ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(responseWrapper,
        ContentCachingResponseWrapper.class);
    if (wrapper != null) {
      byte[] buf = wrapper.getContentAsByteArray();
      if (buf.length > 0) {
        try {
          return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
          return " - ";
        }
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
      doFilterWrapped(new ContentCachingRequestWrapper(request),
          new ContentCachingResponseWrapper(response), filterChain);
    }
  }

  protected void doFilterWrapped(ContentCachingRequestWrapper request,
      ContentCachingResponseWrapper response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
//      logRequest(request);
      filterChain.doFilter(request, response);
    } finally {
      logRequest(request);
      logResponse(response);
      response.copyBodyToResponse();
    }
  }
}