package io.so1s.backend.domain.test.v2.dto.mapper;

import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.deployment.repository.DeploymentRepository;
import io.so1s.backend.domain.test.v2.dto.common.ABNTestElementDto;
import io.so1s.backend.domain.test.v2.entity.ABNTestElement;
import io.so1s.backend.domain.test.v2.repository.ABNTestElementRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ABNTestElementMapperImpl implements ABNTestElementMapper {

  private final ABNTestElementRepository elementRepository;
  private final DeploymentRepository deploymentRepository;

  @Override
  public ABNTestElement toElement(ABNTestElementDto dto) throws DeploymentNotFoundException {

    Deployment deployment = deploymentRepository.findById(dto.getDeploymentId())
        .orElseThrow(() -> new DeploymentNotFoundException(
            String.format("주어진 id %d와(과) 일치하는 Deployment를 찾지 못했습니다.", dto.getDeploymentId())));

    return elementRepository.findByDeployment_Id(deployment.getId())
        .orElseGet(
            () -> elementRepository.save(
                ABNTestElement.builder()
                    .deployment(deployment)
                    .weight(dto.getWeight())
                    .build()));
  }

  @Transactional
  @Override
  public ABNTestElementDto toDto(ABNTestElement entity) {
    return ABNTestElementDto.builder()
        .deploymentId(entity.getDeployment().getId())
        .weight(entity.getWeight())
        .build();
  }
}
