package io.so1s.backend.integration.aws.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.findify.s3mock.S3Mock;
import javax.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.SocketUtils;

@Profile("test")
@TestConfiguration
public class S3MockConfig {

  @PreDestroy
  public void preDestroy() {
    s3Mock().stop();
  }

  @Bean
  public Integer s3MockPort() {
    return SocketUtils.findAvailableTcpPort();
  }

  @Bean
  public S3Mock s3Mock() {
    S3Mock mock = new S3Mock.Builder().withPort(s3MockPort()).withInMemoryBackend()
        .build();
    mock.start();
    return mock;
  }

  @Primary
  @Bean
  public AmazonS3 amazonS3() {
    AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(
        String.format("http://127.0.0.1:%s", s3MockPort()), Regions.AP_NORTHEAST_2.name());
    AmazonS3 client = AmazonS3ClientBuilder
        .standard()
        .withPathStyleAccessEnabled(true)
        .withEndpointConfiguration(endpoint)
        .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
        .build();
    return client;
  }

}
