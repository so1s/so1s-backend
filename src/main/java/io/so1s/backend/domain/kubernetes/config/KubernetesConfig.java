package io.so1s.backend.domain.kubernetes.config;

import io.fabric8.istio.client.DefaultIstioClient;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KubernetesConfig {

  @Bean
  public KubernetesClient kubernetesClient() {
    return new DefaultKubernetesClient();
  }

  @Bean
  public IstioClient istioClient() {
    return new DefaultIstioClient();
  }


}
