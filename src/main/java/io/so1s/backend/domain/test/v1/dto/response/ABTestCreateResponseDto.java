package io.so1s.backend.domain.test.v1.dto.response;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
