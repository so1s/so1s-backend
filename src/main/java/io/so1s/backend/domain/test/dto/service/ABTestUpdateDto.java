package io.so1s.backend.domain.test.dto.service;

import io.so1s.backend.domain.test.entity.ABTest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ABTestUpdateDto {

  private ABTest entity;
  private Boolean success;

}
