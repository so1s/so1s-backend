package io.so1s.backend.domain.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelUploadResponseDto {

  @Builder.Default
  private Boolean success = Boolean.TRUE;
  private String modelName;
  private String fileName;
  private String version;
  private String savedUrl;

}
