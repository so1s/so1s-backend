package io.so1s.backend.domain.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelFindYamlResponseDto {

  @Builder.Default
  private String yaml = "";

}
