package io.so1s.backend.global.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

public class HashGenerator {

  public static String hashGenerateBySha256() {
    try {
      return DatatypeConverter.printHexBinary(
          MessageDigest.getInstance("SHA-256")
              .digest(String.valueOf(new Date().getTime())
                  .getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }
}
