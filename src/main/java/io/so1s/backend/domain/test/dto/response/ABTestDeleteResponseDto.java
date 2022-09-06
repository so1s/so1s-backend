package io.so1s.backend.domain.test.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ABTestDeleteResponseDto {

  @Builder.Default
  private Boolean success = Boolean.TRUE;

  @Builder.Default
  private String message = "";

}
