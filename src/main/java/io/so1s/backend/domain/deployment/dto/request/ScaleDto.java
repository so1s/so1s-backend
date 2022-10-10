package io.so1s.backend.domain.deployment.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScaleDto {

  @NotNull
  private Standard standard;

  @NotNull
  @Min(1)
  private int standardValue;

  @NotNull
  @Min(1)
  private int minReplicas;

  @NotNull
  @Min(1)
  private int maxReplicas;
}
