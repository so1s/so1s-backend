package io.so1s.backend.domain.model.dto.response;

import io.so1s.backend.domain.model.entity.Model;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelFindResponseDto {

  @Builder.Default
  private List<Model> models = new ArrayList<>();
}
