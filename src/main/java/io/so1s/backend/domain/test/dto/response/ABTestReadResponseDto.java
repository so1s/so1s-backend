package io.so1s.backend.domain.test.dto.response;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
