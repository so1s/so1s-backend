package io.so1s.backend.domain.test.v2.dto.common;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ABNTestElementDto {

  @NotNull
  private Long deploymentId;

  @NotNull(message = "인퍼런스 서버 목록이 주어지지 않았습니다.")
  @Min(value = 0, message = "weight는 음수가 될 수 없습니다.")
  private Integer weight;

}
