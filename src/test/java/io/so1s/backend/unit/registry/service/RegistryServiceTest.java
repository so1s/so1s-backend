package io.so1s.backend.unit.registry.service;

import io.so1s.backend.domain.registry.dto.request.RegistryUploadRequestDto;
import io.so1s.backend.domain.registry.service.RegistryService;
import io.so1s.backend.global.config.RegistryDataConfig;
import io.so1s.backend.unit.kubernetes.config.TestKubernetesConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestKubernetesConfig.class, RegistryDataConfig.class})
@ActiveProfiles(profiles = {"test"})
public class RegistryServiceTest {

  @Autowired
  RegistryService registryService;
  @Autowired
  TextEncryptor textEncryptor;

  @Test
  @DisplayName("RegistryService의 Entity Create, Read 관련 메소드가 제대로 동작한다.")
  public void registryServiceTest() {
    RegistryUploadRequestDto requestDto = RegistryUploadRequestDto.builder().baseUrl("ghcr.io")
        .username("username").password("password").build();

    registryService.saveRegistry(requestDto);

    var founds = registryService.findAll();

    Assertions.assertThat(founds).hasSize(1);
    Assertions.assertThat(founds.get(0).getBaseUrl()).isEqualTo("ghcr.io");
    Assertions.assertThat(founds.get(0).getUsername()).isEqualTo("username");
    Assertions.assertThat(textEncryptor.decrypt(founds.get(0).getPassword())).isEqualTo("password");

    var id = founds.get(0).getId();

    var foundById = registryService.findRegistryById(id);

    Assertions.assertThat(foundById).isPresent();
    Assertions.assertThat(foundById.get()).isEqualTo(founds.get(0));

  }

}
