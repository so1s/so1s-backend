package io.so1s.backend.domain.deployment.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
  @Size(min = 1, max = 64, message = "Please enter the length within 1 to 64 characters.")
  private String name;

  @NotNull(message = "Please Input ModelMetadataId.")
  private Long modelMetadataId;

  @Builder.Default
  private String strategy = "rolling";

  private Long resourceId;

  @Valid
  private ScaleDto scale;

}

