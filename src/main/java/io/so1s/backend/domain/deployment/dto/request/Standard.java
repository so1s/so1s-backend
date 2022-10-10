package io.so1s.backend.domain.deployment.dto.request;

public enum Standard {
  LATENCY("m"),
  GPU("%"),
  REPLICAS("");

  private String unit;

  Standard(String unit) {
    this.unit = unit;
  }
}
