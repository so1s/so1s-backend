package io.so1s.backend.domain.test.v1.dto.request;

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
public class ABTestElementDto {

  @NotNull
  private Long id;

  @NotNull
  @Min(value = 0, message = "weight는 음수가 될 수 없습니다.")
  private Integer weight;

}
