package io.so1s.backend.domain.test.v2.dto.response;

import io.so1s.backend.domain.test.v2.dto.common.ABNTestElementDto;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ABNTestReadResponseDto {

  @NotNull
  private Long id;

  @NotBlank
  private String name;


  @NotNull
  @Size(min = 1, message = "하나 이상의 인퍼런스 서버가 필요합니다.")
  private List<ABNTestElementDto> elements;

  @NotBlank
  private String domain;

  @NotBlank
  private String endPoint;

}
