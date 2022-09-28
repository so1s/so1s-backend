package io.so1s.backend.domain.deployment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentFindYamlResponseDto {

  @Builder.Default
  private String yaml = "";

}
