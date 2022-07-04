package io.so1s.backend.domain.model.dto.request;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
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
