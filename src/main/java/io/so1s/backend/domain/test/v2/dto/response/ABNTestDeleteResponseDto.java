package io.so1s.backend.domain.test.v2.dto.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ABNTestDeleteResponseDto {

  @Builder.Default
  private Boolean success = Boolean.TRUE;

  @Builder.Default
  private String message = "";

}
