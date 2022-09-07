package io.so1s.backend.domain.aws.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import io.so1s.backend.domain.aws.Exception.FileConversionException;
import io.so1s.backend.domain.aws.Exception.UnsupportedFileFormatException;
import io.so1s.backend.domain.aws.dto.response.FileSaveResultForm;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileUploadService {

  private final ExternalFileService uploadService;

  public FileSaveResultForm uploadFile(MultipartFile file) {
    String fileName = createFileName(file.getOriginalFilename());
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(file.getSize());
    objectMetadata.setContentType(file.getContentType());
    try (InputStream inputStream = file.getInputStream()) {
      uploadService.uploadFile(inputStream, objectMetadata, fileName);
    } catch (IOException e) {
      throw new FileConversionException(
          String.format("파일 변환 중 에러가 발생하였습니다. (%s)", file.getOriginalFilename()));
    }

    return FileSaveResultForm.builder()
        .originName(file.getName())
        .savedName(fileName)
        .url(uploadService.getFileUrl(fileName))
        .build();
  }

  private String createFileName(String originalFilename) {
    return UUID.randomUUID().toString().concat(getFileExtension(originalFilename));
  }

  public String getFileExtension(String fileName) {
    try {
      return fileName.substring(fileName.lastIndexOf("."));
    } catch (StringIndexOutOfBoundsException e) {
      throw new UnsupportedFileFormatException(
          String.format("잘못된 형식의 파일입니다. 확장자를 명시해주세요. (%s)", fileName));
    }
  }
}
