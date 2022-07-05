package io.so1s.backend.domain.kubernetes.service;

import io.so1s.backend.domain.kubernetes.dto.ImageAuthDto;
import io.so1s.backend.domain.kubernetes.dto.ImageAuthPolicy;

public interface RegistryService {

  ImageAuthPolicy findAuthPolicy(String imageUrl);

  ImageAuthDto authorize(ImageAuthPolicy imageAuthPolicy, String apiKey);

  ImageAuthDto authorize(ImageAuthPolicy imageAuthPolicy, String userName, String password);

}
