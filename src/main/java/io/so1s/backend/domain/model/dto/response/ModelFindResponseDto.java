package io.so1s.backend.domain.model.dto.response;

import io.so1s.backend.global.vo.Status;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelFindResponseDto {

  private LocalDateTime age;
  private String name;
  private Status status;
  private String version;
  private String library;
}
