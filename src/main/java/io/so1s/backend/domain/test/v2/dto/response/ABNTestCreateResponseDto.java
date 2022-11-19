package io.so1s.backend.domain.test.v2.dto.response;


import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ABNTestCreateResponseDto {

  @Builder.Default
  private Boolean success = Boolean.TRUE;

  @NotBlank
  private String message;

  @Nullable
  private ABNTestReadResponseDto data;
}
