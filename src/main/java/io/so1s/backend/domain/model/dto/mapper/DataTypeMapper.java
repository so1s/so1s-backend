package io.so1s.backend.domain.model.dto.mapper;

import io.so1s.backend.domain.model.dto.response.DataTypeResponseDto;
import io.so1s.backend.domain.model.entity.DataType;
import org.springframework.stereotype.Component;

@Component
public class DataTypeMapper {

  public DataTypeResponseDto toDto(DataType entity) {
    return DataTypeResponseDto.builder()
        .name(entity.getName())
        .build();
  }

}
