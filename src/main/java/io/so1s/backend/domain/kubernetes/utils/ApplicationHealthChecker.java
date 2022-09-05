package io.so1s.backend.domain.kubernetes.utils;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApplicationHealthChecker {

  public boolean checkApplicationHealth(String url) {
    try {
      new RestTemplate().getForObject("https://" + url + "/healthz", String.class);
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
