package io.so1s.backend.global.initializer.common;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.so1s.backend.domain.auth.repository.UserRepository;
import io.so1s.backend.domain.auth.service.UserService;
import io.so1s.backend.domain.auth.vo.UserRole;
import io.so1s.backend.global.utils.Base64Mapper;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class AdminInitializer {

  private final KubernetesClient kubernetesClient;
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;
  private final UserRepository userRepository;

  private static final String namespace = "backend";
  private static final String secretName = "backend-admin-secret";

  private void saveAdmin(String username, String password) {
    var user = userRepository.findByUsername(username).orElseGet(() ->
        userService.createUser(username, password, UserRole.ADMIN)
    );

    user.changePassword(passwordEncoder.encode(password));
  }

  @PostConstruct
  private void initAdmin() {
    Optional<Secret> adminSecret = Optional.empty();

    try {
      adminSecret = Optional.ofNullable(
          kubernetesClient.secrets().inNamespace(namespace).withName(secretName).get());
    } catch (KubernetesClientException ignored) {

    }

    adminSecret.ifPresentOrElse((secret) -> {
      var data = secret.getData();

      String username = Base64Mapper.decode(data.get("username"));
      String password = Base64Mapper.decode(data.get("password"));

      saveAdmin(username, password);
    }, () -> {
      String username = "admin";
      String password = KeyGenerators.string().generateKey();

      saveAdmin(username, password);

      Secret secret = new SecretBuilder()
          .withType("Opaque")
          .withNewMetadata()
          .withName(secretName)
          .withNamespace(namespace)
          .endMetadata()
          .addToData("username", Base64Mapper.encode(username))
          .addToData("password", Base64Mapper.encode(password))
          .build();

      kubernetesClient.secrets().inNamespace("backend").createOrReplace(secret);
    });

  }

}
