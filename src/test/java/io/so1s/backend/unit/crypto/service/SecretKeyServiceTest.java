package io.so1s.backend.unit.crypto.service;

import io.so1s.backend.domain.crypto.service.SecretKeyService;
import io.so1s.backend.unit.kubernetes.config.TestKubernetesConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {TestKubernetesConfig.class})
@ActiveProfiles(profiles = {"test"})
@DisplayName("SecretKeyService에서")
public class SecretKeyServiceTest {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(
      SecretKeyServiceTest.class);

  @Autowired
  private SecretKeyService secretKeyService;

  @Test
  @DisplayName("암호화 / 복호화 관련 메소드를 사용할 수 있다.")
  public void testEncodeDecode() {
    String sampleText = "ML 시스템을 시간 소모 없이 쿠버로 이전할 수 있다면?";

    String encoded = secretKeyService.encode(sampleText);
    String decoded = secretKeyService.decode(encoded);

    log.info(String.format("Given: %s", sampleText));
    log.info(String.format("Encoded: %s", encoded));
    log.info(String.format("Decoded: %s", decoded));

    Assertions.assertThat(sampleText)
        .isEqualTo(decoded)
        .isNotEqualTo(encoded);
  }

}
