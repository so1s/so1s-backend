package io.so1s.backend.global.initializer.data;

import io.so1s.backend.domain.deployment.entity.DeploymentStrategy;
import io.so1s.backend.domain.deployment_strategy.repository.DeploymentStrategyRepository;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeploymentStrategyInitializer {

  private final DeploymentStrategyRepository repository;
  private static final List<String> names = List.of("rolling", "static");

  @PostConstruct
  private void addDeploymentStrategies() {
    names.forEach(name -> repository.findByName(name)
        .orElseGet(() -> repository.save(
            DeploymentStrategy.builder()
                .name(name)
                .build())));
  }
}
