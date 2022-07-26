package io.so1s.backend.domain.deployment.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentResponseDto {

  @Builder.Default
  private Boolean success = Boolean.TRUE;

  private Long id;

  private String name;

}
