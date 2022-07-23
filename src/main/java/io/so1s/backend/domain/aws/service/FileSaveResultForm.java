package io.so1s.backend.domain.aws.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileSaveResultForm {

  String fileName;
  String url;
}
