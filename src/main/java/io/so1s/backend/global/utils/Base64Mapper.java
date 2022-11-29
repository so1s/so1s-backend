package io.so1s.backend.global.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class Base64Mapper {

  public static String encode(String plainText) {
    return new String(Base64.getMimeEncoder().encode(plainText.getBytes()), StandardCharsets.UTF_8);
  }

  public static String decode(String encodedText) {
    return new String(Base64.getMimeDecoder().decode(encodedText), StandardCharsets.UTF_8);
  }

}
