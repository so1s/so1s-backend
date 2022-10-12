package io.so1s.backend.domain.deployment.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Standard {
  LATENCY("m", "AverageValue"),
  GPU("%", "AverageUtilization"),
  REPLICAS("", "");

  private String unit;
  private String type;

}
