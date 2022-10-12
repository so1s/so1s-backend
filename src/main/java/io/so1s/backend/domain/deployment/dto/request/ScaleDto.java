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

  @Min(1)
  private int standardValue;

  @Min(1)
  private int minReplicas;
  
  @Min(1)
  private int maxReplicas;
}
