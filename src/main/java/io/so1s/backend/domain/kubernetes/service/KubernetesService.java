package io.so1s.backend.domain.kubernetes.service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.domain.resource.entity.Resource;
import io.so1s.backend.domain.test.entity.ABTest;

public interface KubernetesService {

  HasMetadata getDeploymentObject(String name);

  HasMetadata getJobObject(String name);

  String getWorkloadToYaml(HasMetadata object);

  boolean inferenceServerBuild(ModelMetadata modelMetadata) throws InterruptedException;

  boolean createNamespace(String name);

  boolean createResourceQuota(Resource resource, String namespace);

  boolean createResourceQuotaWithGpu(Resource resource, String namespace);

  boolean deployInferenceServer(
      Deployment deployment);

  boolean deployABTest(ABTest abTest);

  boolean deleteDeployment(Deployment deployment);

  boolean deleteABTest(ABTest abTest);

  boolean createHPA(Deployment deployment,
      String namespace);
}
