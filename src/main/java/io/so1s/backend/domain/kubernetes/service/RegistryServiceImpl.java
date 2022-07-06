package io.so1s.backend.domain.kubernetes.service;

import io.so1s.backend.domain.kubernetes.dto.ImageAuthPolicy;
import io.so1s.backend.domain.model.dto.request.ModelUploadRequestDto;
import org.springframework.stereotype.Service;

@Service
public class RegistryServiceImpl implements RegistryService {

  private String publicAwsBaseUrl = "public.ecr.aws";
  private String privateAwsBaseUrl = "dkr.ecr";

  @Override
  public ImageAuthPolicy findAuthPolicy(
      ModelUploadRequestDto modelUploadRequestDto) {
    // imageUrl로 어떤 인증이 필요한 레지스트리인지 (레지스트리 타입체크)

    String imageUrl = modelUploadRequestDto.getUrl();

    if (imageUrl.contains(publicAwsBaseUrl)) {
      return ImageAuthPolicy.AWS_PUBLIC;
    }

    if (imageUrl.contains(privateAwsBaseUrl)) {
      return ImageAuthPolicy.AWS_PRIVATE;
    }

    if (modelUploadRequestDto.getUserId() != null
        && modelUploadRequestDto.getUserPassword() != null) {
      return ImageAuthPolicy.DOCKERHUB_PRIVATE;
    }

    return ImageAuthPolicy.DOCKERHUB_PUBLIC;
  }
}
