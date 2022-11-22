package io.so1s.backend.domain.test.v2.service;

import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.test.v2.dto.mapper.ABNTestMapper;
import io.so1s.backend.domain.test.v2.dto.request.ABNTestRequestDto;
import io.so1s.backend.domain.test.v2.dto.response.ABNTestDeleteResponseDto;
import io.so1s.backend.domain.test.v2.dto.response.ABNTestReadResponseDto;
import io.so1s.backend.domain.test.v2.dto.service.derived.ABNTestCreateDto;
import io.so1s.backend.domain.test.v2.dto.service.derived.ABNTestUpdateDto;
import io.so1s.backend.domain.test.v2.entity.ABNTest;
import io.so1s.backend.domain.test.v2.exception.ABNTestNotFoundException;
import io.so1s.backend.domain.test.v2.repository.ABNTestRepository;
import io.so1s.backend.domain.test.v2.service.internal.ABNTestKubernetesService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ABNTestServiceImpl implements ABNTestService {

  private final ABNTestKubernetesService kubernetesService;
  private final ABNTestRepository repository;
  private final ABNTestMapper mapper;

  @Override
  public ABNTestCreateDto createABNTest(ABNTestRequestDto requestDto)
      throws DeploymentNotFoundException {

    ABNTest entity = repository.save(mapper.toABNTest(requestDto));
    boolean success = kubernetesService.deployABNTest(entity);

    return ABNTestCreateDto.builder()
        .success(success)
        .entity(entity)
        .build();
  }

  @Override
  public ABNTestUpdateDto updateABNTest(ABNTestRequestDto requestDto)
      throws ABNTestNotFoundException, DeploymentNotFoundException {

    String name = requestDto.getName();

    repository.findByName(name)
        .orElseThrow(() -> new ABNTestNotFoundException(
            String.format("name %s와(과) 일치하는 ABN 테스트를 찾지 못했습니다.", name)));

    ABNTest entity = repository.save(mapper.toABNTest(requestDto));
    boolean success = kubernetesService.deployABNTest(entity);

    return ABNTestUpdateDto.builder()
        .success(success)
        .entity(entity)
        .build();
  }

  @Override
  public ABNTestDeleteResponseDto deleteABNTest(Long id) throws ABNTestNotFoundException {

    ABNTest entity = repository.findById(id)
        .orElseThrow(() -> new ABNTestNotFoundException(
            String.format("id %d와(과) 일치하는 ABN 테스트를 찾지 못했습니다.", id)));

    boolean success = kubernetesService.deleteABNTest(entity);

    repository.delete(entity);

    return ABNTestDeleteResponseDto.builder().success(success).build();
  }

  @Override
  public List<ABNTestReadResponseDto> findAll() {
    return repository.findAll().stream().map(mapper::toReadDto).collect(Collectors.toList());
  }

  @Override
  public Optional<ABNTestReadResponseDto> findById(Long id) {
    return repository.findById(id).map(mapper::toReadDto);
  }
}
