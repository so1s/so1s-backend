package io.so1s.backend.domain.kubernetes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeFindResponseDto {

  @Builder.Default
  private String yaml = "";

}
