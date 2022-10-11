package io.so1s.backend.domain.deployment.dto.request;

import lombok.Getter;

@Getter
public enum Standard {
  LATENCY("m", "AverageValue"),
  GPU("%", "AverageUtilization"),
  REPLICAS("", "");

  private String unit;
  private String type;

  Standard(String unit, String type) {
    this.unit = unit;
    this.type = type;
  }


}
