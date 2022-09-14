package io.so1s.backend.global.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

public class RequestWrapper extends ContentCachingRequestWrapper {

  private byte[] cachedInputStream;

  public RequestWrapper(HttpServletRequest request) throws IOException {
    super(request);
    InputStream requestInputStream = request.getInputStream();
    this.cachedInputStream = StreamUtils.copyToByteArray(requestInputStream);
  }

  @Override
  public ServletInputStream getInputStream() {
    return new ServletInputStream() {
      private InputStream cachedBodyInputStream = new ByteArrayInputStream(cachedInputStream);

      @Override
      public boolean isFinished() {
        try {
          return cachedBodyInputStream.available() == 0;
        } catch (IOException e) {

        }
        return false;
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setReadListener(ReadListener listener) {
        throw new UnsupportedOperationException();
      }

      @Override
      public int read() throws IOException {
        return cachedBodyInputStream.read();
      }
    };
  }
}
