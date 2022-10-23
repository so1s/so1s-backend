package io.so1s.backend.domain.model.controller;

import io.so1s.backend.domain.model.dto.response.DataTypeResponseDto;
import io.so1s.backend.domain.model.service.DataTypeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/data-types")
@RequiredArgsConstructor
public class DataTypeController {

  private final DataTypeService dataTypeService;

  @GetMapping
  public ResponseEntity<List<DataTypeResponseDto>> getDataTypes() {
    return ResponseEntity.ok(dataTypeService.findAllToDto());
  }
}
