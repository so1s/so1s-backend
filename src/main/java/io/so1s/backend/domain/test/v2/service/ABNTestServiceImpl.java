package io.so1s.backend.domain.test.v2.service;

import io.so1s.backend.domain.deployment.exception.DeploymentNotFoundException;
import io.so1s.backend.domain.test.v1.dto.response.ABTestReadResponseDto;
import io.so1s.backend.domain.test.v2.dto.request.ABNTestRequestDto;
import io.so1s.backend.domain.test.v2.dto.response.ABNTestDeleteResponseDto;
import io.so1s.backend.domain.test.v2.dto.service.derived.ABNTestCreateDto;
import io.so1s.backend.domain.test.v2.dto.service.derived.ABNTestUpdateDto;
import io.so1s.backend.domain.test.v2.exception.ABNTestNotFoundException;
import io.so1s.backend.domain.test.v2.repository.ABNTestRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ABNTestServiceImpl implements ABNTestService {

  private final ABNTestRepository abnTestRepository;

  @Override
  public ABNTestCreateDto createABNTest(ABNTestRequestDto requestDto)
      throws DeploymentNotFoundException {
    return null;
  }

  @Override
  public ABNTestUpdateDto updateABNTest(ABNTestRequestDto requestDto)
      throws ABNTestNotFoundException, DeploymentNotFoundException {
    return null;
  }

  @Override
  public ABNTestDeleteResponseDto deleteABNTest(Long id) throws ABNTestNotFoundException {
    return null;
  }

  @Override
  public List<ABTestReadResponseDto> findAll() {
    return null;
  }

  @Override
  public Optional<ABTestReadResponseDto> findbyId(Long id) {
    return Optional.empty();
  }
}
