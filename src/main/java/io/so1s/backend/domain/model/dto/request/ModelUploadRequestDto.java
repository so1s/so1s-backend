package io.so1s.backend.domain.model.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

  @NotNull(message = "Please upload model file.")
  private MultipartFile modelFile;

  @NotBlank
  @Size(min = 1, max = 64, message = "Please enter the length within 1 to 64 characters.")
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

  @NotBlank
  private String type;
}
