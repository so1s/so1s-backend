package io.so1s.backend.unit.registry.config;

import com.sun.xml.bind.v2.runtime.unmarshaller.Base64Data;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.so1s.backend.domain.registry.config.RegistryConfig;
import io.so1s.backend.domain.registry.entity.Registry;
import io.so1s.backend.domain.registry.service.RegistryKubernetesService;
import io.so1s.backend.domain.registry.service.RegistryKubernetesServiceImpl;
import io.so1s.backend.global.utils.Base64Mapper;
import java.util.Base64;
import java.util.Optional;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = {"test"})
public class RegistryConfigTest {

  @MockBean
  private RegistryKubernetesService service;

  private RegistryConfig registryConfig;

  private void setMockSecretResult(Secret secret) {
    service = Mockito.mock(RegistryKubernetesServiceImpl.class);

    Mockito
        .when(service.getDefaultSecret())
        .thenReturn(Optional.ofNullable(secret));
  }

  @Test
  @DisplayName("Default registry secret이 존재할 경우, RegistryConfig.defaultRegistry() Bean이 문제 없이 생성된다.")
  public void beanCreationSuccessTest() {
    // given

    Secret secret = new SecretBuilder()
        .addToData("username", Base64Mapper.encode("example-username"))
        .addToData("password", Base64Mapper.encode("example-password"))
        .build();

    setMockSecretResult(secret);
    registryConfig = new RegistryConfig(service);

    // when

    Registry registry = registryConfig.defaultRegistry();

    // then

    AssertionsForClassTypes.assertThat(registry.getUsername())
        .isEqualTo(Base64Mapper.decode(secret.getData().get("username"))).isEqualTo("example-username");
    AssertionsForClassTypes.assertThat(registry.getPassword())
        .isEqualTo(Base64Mapper.decode(secret.getData().get("password"))).isEqualTo("example-password");
  }

  @Test
  @DisplayName("Default registry secret이 존재하지 않을 경우, RegistryConfig.defaultRegistry() Bean 생성 오류가 발생한다.")
  public void beanCreationNotExistsFailureTest() {
    // glven

    setMockSecretResult(null);
    registryConfig = new RegistryConfig(service);

    // then
    Assertions.assertThrowsExactly(IllegalStateException.class, () ->
        // when
        registryConfig.defaultRegistry()
    );
  }


  @Test
  @DisplayName("Default registry secret username 필드가 없을 경우, RegistryConfig.defaultRegistry() Bean 생성 오류가 발생한다.")
  public void beanCreationUsernameNotGivenFailureTest() {
    // glven

    Secret secret = new SecretBuilder()
        .addToData("password", "example-password")
        .build();

    setMockSecretResult(secret);
    registryConfig = new RegistryConfig(service);

    // then
    Assertions.assertThrowsExactly(IllegalStateException.class, () ->
        // when
        registryConfig.defaultRegistry()
    );
  }

  @Test
  @DisplayName("Default registry secret password 필드가 없을 경우, RegistryConfig.defaultRegistry() Bean 생성 오류가 발생한다.")
  public void beanCreationPasswordNotGivenFailureTest() {
    // glven

    Secret secret = new SecretBuilder()
        .addToData("username", "example-username")
        .build();

    setMockSecretResult(null);
    registryConfig = new RegistryConfig(service);

    // then
    Assertions.assertThrowsExactly(IllegalStateException.class, () ->
        // when
        registryConfig.defaultRegistry()
    );
  }
}
