package io.so1s.backend.domain.aws.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.InputStream;

public interface ExternalFileService {

  void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName);

  String getFileUrl(String fileName);

  void deleteFile(String url);
}
