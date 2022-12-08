package io.so1s.backend.domain.test.v1.service.internal;

import io.fabric8.istio.api.networking.v1beta1.Gateway;
import io.fabric8.istio.api.networking.v1beta1.GatewayBuilder;
import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.api.networking.v1beta1.VirtualServiceBuilder;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.kubernetes.service.NamespaceService;
import io.so1s.backend.domain.test.v1.entity.ABTest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ABTestKubernetesServiceImpl implements
    ABTestKubernetesService {

  private final NamespaceService namespaceService;
  private final KubernetesService kubernetesService;
  private final KubernetesClient client;
  private final IstioClient istioClient;

  @Transactional(readOnly = true)
  @Override
  public boolean deployABTest(ABTest abTest) {
    String namespace = namespaceService.getNamespace();
    String abTestName = "ab-test-" + abTest.getName().toLowerCase();

    String host = abTestName + ".so1s.io"; // TODO: Fix hard-coded root domain

    String aName = abTest.getA().getName().toLowerCase();
    String bName = abTest.getB().getName().toLowerCase();

    Map<String, String> labels = new HashMap<>();
    labels.put("app", "ab-test");
    labels.put("name", abTestName);

    VirtualService abTestVirtualService = new VirtualServiceBuilder()
        .withNewMetadata()
        .withName(abTestName)
        .withNamespace(namespace)
        .addToLabels(labels)
        .endMetadata()
        .withNewSpec()
        .withHosts(host)
        .withGateways(abTestName)
        .addNewHttp()
        .addNewMatch()
        .withNewUri()
        .withNewStringMatchPrefixType("/")
        .endUri()
        .endMatch()
        .addNewRoute()
        .withWeight(50)
        .withNewDestination()
        .withHost(aName)
        .withNewPort()
        .withNumber(3000)
        .endPort()
        .endDestination()
        .endRoute()
        .addNewRoute()
        .withWeight(50)
        .withNewDestination()
        .withHost(bName)
        .withNewPort()
        .withNumber(3000)
        .endPort()
        .endDestination()
        .endRoute()
        .endHttp()
        .endSpec()
        .build();

    Gateway abTestGateway = new GatewayBuilder()
        .withNewMetadata()
        .withName(abTestName)
        .withNamespace(namespace)
        .addToLabels(labels)
        .endMetadata()
        .withNewSpec()
        .addNewServer()
        .withNewPort()
        .withNumber(80)
        .withName("http")
        .withProtocol("HTTP")
        .endPort()
        .withHosts(host)
        .endServer()
        .addNewServer()
        .withNewPort()
        .withNumber(9443)
        .withName("http-dev")
        .withProtocol("HTTP")
        .endPort()
        .withHosts(host)
        .endServer()
        .endSpec()
        .build();

    try {
      istioClient.v1beta1().gateways().inNamespace(namespace).createOrReplace(abTestGateway);
      istioClient.v1beta1().virtualServices().inNamespace(namespace)
          .createOrReplace(abTestVirtualService);
    } catch (KubernetesClientException ignored) {
      return false;
    }

    return true;
  }


  @Override
  public boolean deleteABTest(ABTest abTest) {
    String namespace = namespaceService.getNamespace();
    String abTestName = "ab-test-" + abTest.getName().toLowerCase();

    try {
      istioClient.v1beta1().gateways().inNamespace(namespace).withName(abTestName).delete();
      istioClient.v1beta1().virtualServices().inNamespace(namespace).withName(abTestName).delete();
    } catch (KubernetesClientException ignored) {
      return false;
    }

    return true;
  }
}
