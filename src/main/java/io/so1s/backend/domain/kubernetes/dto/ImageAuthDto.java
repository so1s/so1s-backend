package io.so1s.backend.domain.kubernetes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageAuthDto {

  @Builder.Default
  private String apikey = "";

  @Builder.Default
  private String userName = "";

  @Builder.Default
  private String password = "";

  @Builder.Default
  private ImageAuthPolicy authPolicy = ImageAuthPolicy.NONE;
}
