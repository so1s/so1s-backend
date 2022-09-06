package io.so1s.backend.domain.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.so1s.backend.domain.aws.config.S3Config;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AwsS3Service implements ExternalFileService {

  private final AmazonS3 amazonS3;

  private final S3Config s3Config;

  @Override
  public void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName) {
    amazonS3.putObject(new PutObjectRequest(s3Config.getBucket(), fileName, inputStream,
        objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
  }

  @Override
  public String getFileUrl(String fileName) {
    return amazonS3.getUrl(s3Config.getBucket(), fileName).toString();
  }

  @Override
  public void deleteFile(String url) {
    URI uri = null;

    try {
      uri = new URI(url);
    } catch (URISyntaxException ignored) {
      return;
    }

    String fileName = uri.getPath().substring(1);

    amazonS3.deleteObject(new DeleteObjectRequest(s3Config.getBucket(), fileName));
  }
}
