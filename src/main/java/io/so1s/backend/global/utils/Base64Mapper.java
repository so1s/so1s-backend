package io.so1s.backend.global.utils;

import java.util.Arrays;
import java.util.Base64;

public class Base64Mapper {

  public static String encode(String plainText) {
    return Arrays.toString(Base64.getMimeEncoder().encode(plainText.getBytes()));
  }

  public static String decode(String encodedText) {
    return Arrays.toString(Base64.getMimeDecoder().decode(encodedText));
  }

}
