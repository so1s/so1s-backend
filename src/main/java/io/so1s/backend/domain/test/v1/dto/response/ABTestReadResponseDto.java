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
public class ABTestReadResponseDto {

  @NotNull
  private Long id;

  @NotBlank
  private String name;

  @NotNull
  private Long aId;

  @NotNull
  private Long bId;

  @NotBlank
  private String domain;

}
