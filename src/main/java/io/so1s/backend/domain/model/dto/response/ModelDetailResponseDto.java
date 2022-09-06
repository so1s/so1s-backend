package io.so1s.backend.domain.model.dto.response;


import io.so1s.backend.domain.auth.vo.Status;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDetailResponseDto {

  private LocalDateTime age;
  private String name;
  private String version;
  private Status status;
  private String url;
  private String library;
  private String inputShape;
  private String inputDtype;
  private String outputShape;
  private String outputDtype;
}
