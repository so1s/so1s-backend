package io.so1s.backend.domain.resource.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCreateResponseDto {

  @Builder.Default
  private Boolean success = Boolean.TRUE;

  @Builder.Default
  private String message = "";
}
