package io.so1s.backend.domain.test.v1.service.internal;

import io.so1s.backend.domain.test.v1.entity.ABTest;

public interface ABTestKubernetesService {

  boolean deployABTest(ABTest abTest);

  boolean deleteABTest(ABTest abTest);

}
