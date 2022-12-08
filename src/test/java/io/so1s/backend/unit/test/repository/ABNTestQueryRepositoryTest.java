package io.so1s.backend.unit.test.repository;

import io.so1s.backend.domain.test.v2.repository.ABNTestRepository;
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
@SpringBootTest
@ActiveProfiles(profiles = {"test"})
public class ABNTestQueryRepositoryTest {

  @Autowired
  private ABNTestRepository repository;

  @Test
  @DisplayName("ABNTestRepository.findAllByDeploymentId() 테스트")
  public void entitiesTest() {
    var tests = repository.findAllByDeploymentId(42L);

    Assertions.assertThat(tests).isEmpty();

  }


}
