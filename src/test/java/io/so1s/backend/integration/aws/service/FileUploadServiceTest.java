package io.so1s.backend.integration.aws.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.amazonaws.services.s3.AmazonS3;
import io.findify.s3mock.S3Mock;
import io.so1s.backend.domain.aws.config.S3Config;
import io.so1s.backend.domain.aws.service.AwsS3UploadService;
import io.so1s.backend.domain.aws.service.FileSaveResultForm;
import io.so1s.backend.domain.aws.service.FileUploadService;
import java.io.FileInputStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

@Import(S3MockConfig.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest
public class FileUploadServiceTest {

  private static String BUCKET_NAME;
  private static AwsS3UploadService awsS3UploadService;
  private static FileUploadService fileUploadService;

  @BeforeAll
  static void setUp(@Autowired S3Config s3Config, @Autowired S3Mock s3Mock,
      @Autowired AmazonS3 amazonS3) {
    BUCKET_NAME = s3Config.getBucket();
    s3Mock.start();
    amazonS3.createBucket(BUCKET_NAME);
    awsS3UploadService = new AwsS3UploadService(amazonS3, s3Config);
    fileUploadService = new FileUploadService(awsS3UploadService);
  }

  @AfterAll
  static void tearDown(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
    amazonS3.shutdown();
    s3Mock.stop();
  }

  @Test
  @DisplayName("S3 파일 업로드 테스트입니다.")
  public void uploadFileTest() throws Exception {
    // given
    String fileName = "testFileName";
    String path = "forTest/titanic_e500.h5";
    MockMultipartFile mockMultipartFile = new MockMultipartFile(
        fileName,
        path,
        "application/octet-stream",
        new FileInputStream(path));

    // when
    FileSaveResultForm result = fileUploadService.uploadFile(mockMultipartFile);

    // then
    assertThat(result.getOriginName()).isEqualTo(mockMultipartFile.getName());
    System.out.println(result.getUrl());
  }

  @Test
  @DisplayName("확장자 없이 파일을 업로드하면 IllegalArgumentException이 발생한다.")
  public void uploadFileIllegalArgumentExceptionTest() throws Exception {
    // given
    String fileName = "testFileName";
    String path = "forTest/titanic_e500";
    MockMultipartFile mockMultipartFile = new MockMultipartFile(
        fileName,
        path,
        "application/octet-stream",
        new FileInputStream(path));

    // when
    // then
    assertThrows(IllegalArgumentException.class, () -> {
      fileUploadService.uploadFile(mockMultipartFile);
    });
  }
}
