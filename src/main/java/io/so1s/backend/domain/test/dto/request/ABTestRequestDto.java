package io.so1s.backend.domain.test.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ABTestRequestDto {

  @NotBlank(message = "name은 공백으로만 이루어지지 않은 3자 이상, 100자 이하 문자열로 이루어져야 합니다.")
  @Size(min = 3, max = 100)
  private String name;

  @NotNull(message = "aId가 주어지지 않았습니다.")
  private Long aId;

  @NotNull(message = "bId가 주어지지 않았습니다.")
  private Long bId;

  @NotBlank(message = "domain이 주어지지 않았습니다.")
  private String domain;

}
