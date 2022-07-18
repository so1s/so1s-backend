package io.so1s.backend.global.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

public class HashGenerator {

  public static String sha1() {
    try {
      return DatatypeConverter.printHexBinary(
          MessageDigest.getInstance("SHA-1")
              .digest(NanoIdUtils.randomNanoId().getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }
}
