package io.so1s.backend.domain.test.v1.dto.service.base;

import io.so1s.backend.domain.test.v1.entity.ABTest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ABTestBaseDto {

  private ABTest entity;
  private Boolean success;

}
