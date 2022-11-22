package io.so1s.backend.domain.test.v2.service.internal;

import io.fabric8.istio.api.networking.v1alpha3.Gateway;
import io.fabric8.istio.api.networking.v1alpha3.GatewayBuilder;
import io.fabric8.istio.api.networking.v1alpha3.VirtualService;
import io.fabric8.istio.api.networking.v1alpha3.VirtualServiceBuilder;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.so1s.backend.domain.kubernetes.service.KubernetesService;
import io.so1s.backend.domain.test.v2.entity.ABNTest;
import io.so1s.backend.domain.test.v2.entity.ABNTestElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ABNTestKubernetesServiceImpl implements
    ABNTestKubernetesService {

  private final KubernetesService kubernetesService;
  private final KubernetesClient client;
  private final IstioClient istioClient;

  @Transactional
  @Override
  public boolean deployABNTest(ABNTest abnTest) {
    String namespace = kubernetesService.getNamespace();
    String fullName = "abn-test-" + abnTest.getName().toLowerCase();

    String endpoint = abnTest.getEndPoint();

    List<ABNTestElement> elements = abnTest.getElements();

    Map<String, String> labels = new HashMap<>();
    labels.put("app", "abn-test");
    labels.put("name", fullName);

    var progress = new VirtualServiceBuilder()
        .withNewMetadata()
        .withName(fullName)
        .withNamespace(namespace)
        .addToLabels(labels)
        .endMetadata()
        .withNewSpec()
        .withHosts(endpoint)
        .withGateways(fullName)
        .addNewHttp()
        .addNewMatch()
        .withNewUri()
        .withNewStringMatchPrefixType("/")
        .endUri()
        .endMatch();

    for (var element : elements) {
      progress = progress.addNewRoute()
          .withWeight(element.getWeight())
          .withNewDestination()
          .withHost("inference-" + element.getDeployment().getName().toLowerCase())
          .withNewPort()
          .withNumber(3000)
          .endPort()
          .endDestination()
          .endRoute();
    }

    VirtualService virtualService = progress
        .endHttp()
        .endSpec()
        .build();

    Gateway gateway = new GatewayBuilder()
        .withNewMetadata()
        .withName(fullName)
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
        .withHosts(endpoint)
        .endServer()
        .addNewServer()
        .withNewPort()
        .withNumber(9443)
        .withName("http-dev")
        .withProtocol("HTTP")
        .endPort()
        .withHosts(endpoint)
        .endServer()
        .endSpec()
        .build();

    try {
      istioClient.v1alpha3().gateways().inNamespace(namespace).createOrReplace(gateway);
      istioClient.v1alpha3().virtualServices().inNamespace(namespace)
          .createOrReplace(virtualService);
    } catch (KubernetesClientException ignored) {
      return false;
    }

    return true;
  }

  @Override
  public boolean deleteABNTest(ABNTest abTest) {
    String namespace = kubernetesService.getNamespace();
    String abTestName = "abn-test-" + abTest.getName().toLowerCase();

    try {
      istioClient.v1alpha3().gateways().inNamespace(namespace).withName(abTestName).delete();
      istioClient.v1alpha3().virtualServices().inNamespace(namespace).withName(abTestName).delete();
    } catch (KubernetesClientException ignored) {
      return false;
    }

    return true;
  }
}
