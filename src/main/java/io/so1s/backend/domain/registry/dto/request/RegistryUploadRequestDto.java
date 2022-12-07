package io.so1s.backend.domain.registry.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegistryUploadRequestDto {

  @NotBlank
  private String name;
  @NotBlank
  private String baseUrl;
  @NotBlank
  private String username;
  @NotBlank
  private String password;

}
