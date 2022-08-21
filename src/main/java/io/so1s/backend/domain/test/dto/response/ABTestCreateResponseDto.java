package io.so1s.backend.domain.test.dto.response;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ABTestCreateResponseDto {

  @Builder.Default
  private Boolean success = Boolean.TRUE;

  @NotBlank
  private String message;

  @NotNull
  private ABTestReadResponseDto data;
}
