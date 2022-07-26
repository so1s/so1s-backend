package io.so1s.backend.domain.deployment.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentRequestDto {


  @NotBlank
  @Size(min = 3, max = 100)
  private String name;

  @NotBlank
  private Long modelMetadataId;

  @NotBlank
  private String modelVersion;

  @Builder.Default
  private String strategy = "rolling";

  @NotBlank
  private ResourceRequestDto resources;

}
