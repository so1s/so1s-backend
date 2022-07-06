package io.so1s.backend.global.config;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KubernetesConfig {

  @Bean
  public KubernetesClient kubernetesClient() {
    return new DefaultKubernetesClient();
  }

  @Bean
  @Qualifier("kubernetesNamespace")
  public String kubernetesNamespace() {
    return "default";
  }
}
