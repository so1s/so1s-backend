package io.so1s.backend.global.utils;

import io.so1s.backend.domain.test.v2.dto.request.ABNTestRequestDto;

public class ResourceTemplateStringBuilder {

  public static String ABNTestEndpoint(ABNTestRequestDto requestDto) {
    return String.format("abn-test-%s.%s", requestDto.getName().toLowerCase(),
        requestDto.getDomain());
  }
}
