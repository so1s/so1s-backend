package io.so1s.backend.global.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

public class HashGenerator {

  public static String sha256() {
    try {
      return DatatypeConverter.printHexBinary(
              MessageDigest.getInstance("SHA-256")
                  .digest(NanoIdUtils.randomNanoId().getBytes(StandardCharsets.UTF_8)))
          .substring(0, 32);
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }
}
