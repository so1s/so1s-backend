package io.so1s.backend.domain.kubernetes.service;

import io.so1s.backend.domain.kubernetes.dto.ImageAuthDto;
import io.so1s.backend.domain.kubernetes.dto.ImageAuthPolicy;
import org.springframework.stereotype.Service;

@Service
public class RegistryServiceImpl implements RegistryService {

  @Override
  public ImageAuthPolicy findAuthPolicy(String imageUrl) {
    return null;
  }

  @Override
  public ImageAuthDto authorize(ImageAuthPolicy imageAuthPolicy, String apiKey) {
    return null;
  }

  @Override
  public ImageAuthDto authorize(ImageAuthPolicy imageAuthPolicy, String userName, String password) {
    return null;
  }
}
