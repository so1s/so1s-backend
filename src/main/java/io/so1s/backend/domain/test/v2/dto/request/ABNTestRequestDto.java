package io.so1s.backend.domain.test.v2.dto.request;

import io.so1s.backend.domain.test.v2.dto.common.ABNTestElementDto;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ABNTestRequestDto {

  @NotBlank(message = "name은 공백으로만 이루어지지 않은 3자 이상, 100자 이하의 문자열로 이루어져야 합니다.")
  @Size(min = 3, max = 100, message = "name은 3자 이상, 100자 이하의 문자열로 이루어져야 합니다.")
  private String name;

  @NotNull(message = "인퍼런스 서버 목록이 주어지지 않았습니다.")
  @Size(min = 1, message = "하나 이상의 인퍼런스 서버가 필요합니다.")
  private List<ABNTestElementDto> elements;

  @NotBlank(message = "domain이 주어지지 않았습니다.")
  private String domain;

}
