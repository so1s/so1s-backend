package io.so1s.backend.domain.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelUploadResponseDto {

  @Builder.Default
  private Boolean success = Boolean.TRUE;

  private String modelName = "";

}
