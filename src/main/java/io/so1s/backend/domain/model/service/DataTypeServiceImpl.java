package io.so1s.backend.domain.model.service;

import io.so1s.backend.domain.model.dto.mapper.DataTypeMapper;
import io.so1s.backend.domain.model.dto.response.DataTypeResponseDto;
import io.so1s.backend.domain.model.entity.DataType;
import io.so1s.backend.domain.model.exception.DataTypeNotFoundException;
import io.so1s.backend.domain.model.repository.DataTypeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataTypeServiceImpl implements DataTypeService {

  private final DataTypeRepository dataTypeRepository;
  private final DataTypeMapper dataTypeMapper;


  @Override
  public DataType findDataTypeByName(String name) throws DataTypeNotFoundException {
    return dataTypeRepository.findByName(name).orElseThrow(() -> new DataTypeNotFoundException(
        String.format("Corresponding Input / Output DataType Entity not found with given name %s",
            name)));
  }

  @Override
  public List<DataType> findAll() {
    return dataTypeRepository.findAll();
  }

  @Override
  public List<DataTypeResponseDto> findAllToDto() {
    return findAll().stream().map(dataTypeMapper::toDto).collect(Collectors.toList());
  }

}
