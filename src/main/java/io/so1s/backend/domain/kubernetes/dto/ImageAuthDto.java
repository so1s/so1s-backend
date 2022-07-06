package io.so1s.backend.domain.kubernetes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageAuthDto {

  @Builder.Default
  private String userId = "";

  @Builder.Default
  private String userPassword = "";

  @Builder.Default
  private ImageAuthPolicy authPolicy = ImageAuthPolicy.DOCKERHUB_PUBLIC;
}
