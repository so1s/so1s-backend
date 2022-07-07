package io.so1s.backend.global.domain.healthcheck.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthCheckResponseDto {

  @Builder.Default
  public Boolean success = Boolean.TRUE;
}
