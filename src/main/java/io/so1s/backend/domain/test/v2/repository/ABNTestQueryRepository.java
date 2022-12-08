package io.so1s.backend.domain.test.v2.repository;

import io.so1s.backend.domain.test.v2.entity.ABNTest;
import java.util.List;

public interface ABNTestQueryRepository {

  List<ABNTest> findAllByDeploymentId(Long deploymentId);

}
