package io.so1s.backend.global.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component
public class JsonMapper {

  @Autowired
  @Qualifier("camelObjectMapper")
  private ObjectMapper objectMapper;

  public String asJsonString(final Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      return "";
    }
  }

  public <T> T fromMvcResult(MvcResult result, Class<T> clazz)
      throws JsonProcessingException, UnsupportedEncodingException {
    return objectMapper.readValue(result.getResponse().getContentAsString(), clazz);
  }

  public <T> T fromMvcResult(MvcResult result, TypeReference<T> typeReference)
      throws JsonProcessingException, UnsupportedEncodingException {
    return objectMapper.readValue(result.getResponse().getContentAsString(), typeReference);
  }
}
