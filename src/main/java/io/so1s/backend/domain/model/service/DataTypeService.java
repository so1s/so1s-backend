package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.model.dto.response.DataTypeResponseDto;
import io.so1s.backend.domain.model.entity.DataType;
import io.so1s.backend.domain.model.exception.DataTypeNotFoundException;
import java.util.List;

public interface DataTypeService {

  DataType findDataTypeByName(String name) throws DataTypeNotFoundException;

  List<DataType> findAll();

  List<DataTypeResponseDto> findAllToDto();


}
