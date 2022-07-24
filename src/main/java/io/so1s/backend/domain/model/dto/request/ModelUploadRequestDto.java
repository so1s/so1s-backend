package io.so1s.backend.domain.model.dto.request;

import io.so1s.backend.domain.model.entity.Model;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelUploadRequestDto {

  @NotBlank
  private MultipartFile modelFile;

  @NotBlank
  @Size(min = 1, max = 30)
  private String name;

  @NotBlank
  private String library;

  @NotBlank
  private String inputShape;

  @NotBlank
  private String inputDtype;

  @NotBlank
  private String outputShape;

  @NotBlank
  private String outputDtype;

  public Model toModelEntity() {
    return Model.builder()
        .name(name)
        .library(library)
        .build();
  }
}
