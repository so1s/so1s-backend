package io.so1s.backend.unit.kubernetes.config;

import io.fabric8.istio.client.IstioClient;
import io.fabric8.istio.mock.EnableIstioMockClient;
import io.fabric8.istio.mock.IstioMockServer;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMixedDispatcher;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.fabric8.mockwebserver.Context;
import io.fabric8.mockwebserver.ServerRequest;
import io.fabric8.mockwebserver.ServerResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
@EnableKubernetesMockClient(crud = true)
@EnableIstioMockClient(crud = true)
public class TestKubernetesConfig {

  @Bean
  @Primary
  public KubernetesClient kubernetesClient() {
    final Map<ServerRequest, Queue<ServerResponse>> responses = new HashMap<>();

    KubernetesMockServer kubernetesMockServer = new KubernetesMockServer(
        new Context(), new MockWebServer(), responses, new KubernetesMixedDispatcher(responses),
        false
    );
    kubernetesMockServer.start();

    KubernetesClient kubernetesClient = kubernetesMockServer.createClient();

    Namespace defaultNamespace = new NamespaceBuilder().withNewMetadata().withName("default")
        .addToLabels("istio-injection", "enabled")
        .endMetadata()
        .build();
    kubernetesClient.namespaces().create(defaultNamespace);

    return kubernetesClient;
  }

  @Bean
  @Primary
  public IstioClient istioClient() {
    final Map<ServerRequest, Queue<ServerResponse>> responses = new HashMap<>();

    IstioMockServer istioMockServer = new IstioMockServer(new Context(), new MockWebServer(),
        responses, new KubernetesMixedDispatcher(responses), false);
    istioMockServer.start();

    return istioMockServer.createIstio();
  }

}
