package io.so1s.backend.domain.aws.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileSaveResultForm {

  private String originName;
  private String savedName;
  private String url;
}
