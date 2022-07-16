package io.so1s.backend.domain.model.dto.request;

import io.so1s.backend.domain.model.entity.Model;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelUploadRequestDto {

  @NotBlank
  @Size(min = 3, max = 100)
  private String name;

  @NotBlank
  private String url;

  @NotBlank
  private String library;

  private String info;

  public Model toEntity() {
    return Model.builder()
        .name(name)
        .library(library)
        .build();
  }
}
