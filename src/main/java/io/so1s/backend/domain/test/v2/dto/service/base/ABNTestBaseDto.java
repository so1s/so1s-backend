package io.so1s.backend.domain.test.v2.dto.service.base;

import io.so1s.backend.domain.test.v2.entity.ABNTest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ABNTestBaseDto {

  private ABNTest entity;
  private Boolean success;

}
