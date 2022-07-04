package io.so1s.backend.domain.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelUploadRequestDto {

  @NotNull
  private String modelName = "";
  @NotNull
  private String url = "";
  @NotNull
  private String info = "";
  @NotNull
  private String version = "";
}
