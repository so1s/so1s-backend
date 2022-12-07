package io.so1s.backend.domain.registry.dto.response;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegistryFindResponseDto {

  @NotNull
  private Long id;
  @NotBlank
  private String name;
  @NotBlank
  private String baseUrl;
  @NotBlank
  private String username;

}
