package io.so1s.backend.domain.kubernetes.service;

import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.entity.Resource;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.test.entity.ABTest;

public interface KubernetesService {

  boolean inferenceServerBuild(ModelMetadata modelMetadata);

  boolean createNamespace(String name);

  boolean createResourceQuota(Resource resource, String namespace);

  boolean createResourceQuotaWithGpu(Resource resource, String namespace);

  boolean deployInferenceServer(
      Deployment deployment);

  boolean deployABTest(ABTest abTest);
}
