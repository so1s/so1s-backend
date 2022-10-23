package io.so1s.backend.domain.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataTypeResponseDto {

  @Builder.Default
  private String name = "";
}
