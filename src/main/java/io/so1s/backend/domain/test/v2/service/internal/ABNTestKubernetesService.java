package io.so1s.backend.domain.test.v2.service.internal;

import io.so1s.backend.domain.test.v2.entity.ABNTest;

public interface ABNTestKubernetesService {

  boolean deployABNTest(ABNTest abTest);

  boolean deleteABNTest(ABNTest abTest);

}
