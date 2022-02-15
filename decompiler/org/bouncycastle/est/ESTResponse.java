package org.bouncycastle.est;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;

public class ESTResponse {
  private final ESTRequest originalRequest;
  
  private final HttpUtil.Headers headers;
  
  private final byte[] lineBuffer;
  
  private final Source source;
  
  private String HttpVersion;
  
  private int statusCode;
  
  private String statusMessage;
  
  private InputStream inputStream;
  
  private Long contentLength;
  
  private long read = 0L;
  
  private Long absoluteReadLimit;
  
  private static final Long ZERO = Long.valueOf(0L);
  
  public ESTResponse(ESTRequest paramESTRequest, Source paramSource) throws IOException {
    this.originalRequest = paramESTRequest;
    this.source = paramSource;
    if (paramSource instanceof LimitedSource)
      this.absoluteReadLimit = ((LimitedSource)paramSource).getAbsoluteReadLimit(); 
    Set set = Properties.asKeySet("org.bouncycastle.debug.est");
    if (set.contains("input") || set.contains("all")) {
      this.inputStream = new PrintingInputStream(paramSource.getInputStream());
    } else {
      this.inputStream = paramSource.getInputStream();
    } 
    this.headers = new HttpUtil.Headers();
    this.lineBuffer = new byte[1024];
    process();
  }
  
  private void process() throws IOException {
    this.HttpVersion = readStringIncluding(' ');
    this.statusCode = Integer.parseInt(readStringIncluding(' '));
    this.statusMessage = readStringIncluding('\n');
    for (String str = readStringIncluding('\n'); str.length() > 0; str = readStringIncluding('\n')) {
      int i = str.indexOf(':');
      if (i > -1) {
        String str1 = Strings.toLowerCase(str.substring(0, i).trim());
        this.headers.add(str1, str.substring(i + 1).trim());
      } 
    } 
    this.contentLength = getContentLength();
    if (this.statusCode == 204 || this.statusCode == 202)
      if (this.contentLength == null) {
        this.contentLength = Long.valueOf(0L);
      } else if (this.statusCode == 204 && this.contentLength.longValue() > 0L) {
        throw new IOException("Got HTTP status 204 but Content-length > 0.");
      }  
    if (this.contentLength == null)
      throw new IOException("No Content-length header."); 
    if (this.contentLength.equals(ZERO))
      this.inputStream = new InputStream() {
          public int read() throws IOException {
            return -1;
          }
        }; 
    if (this.contentLength != null) {
      if (this.contentLength.longValue() < 0L)
        throw new IOException("Server returned negative content length: " + this.absoluteReadLimit); 
      if (this.absoluteReadLimit != null && this.contentLength.longValue() >= this.absoluteReadLimit.longValue())
        throw new IOException("Content length longer than absolute read limit: " + this.absoluteReadLimit + " Content-Length: " + this.contentLength); 
    } 
    this.inputStream = wrapWithCounter(this.inputStream, this.absoluteReadLimit);
    if ("base64".equalsIgnoreCase(getHeader("content-transfer-encoding")))
      this.inputStream = new CTEBase64InputStream(this.inputStream, getContentLength()); 
  }
  
  public String getHeader(String paramString) {
    return this.headers.getFirstValue(paramString);
  }
  
  protected InputStream wrapWithCounter(final InputStream in, final Long absoluteReadLimit) {
    return new InputStream() {
        public int read() throws IOException {
          int i = in.read();
          if (i > -1) {
            ESTResponse.this.read++;
            if (absoluteReadLimit != null && ESTResponse.this.read >= absoluteReadLimit.longValue())
              throw new IOException("Absolute Read Limit exceeded: " + absoluteReadLimit); 
          } 
          return i;
        }
        
        public void close() throws IOException {
          if (ESTResponse.this.contentLength != null && ESTResponse.this.contentLength.longValue() - 1L > ESTResponse.this.read)
            throw new IOException("Stream closed before limit fully read, Read: " + ESTResponse.this.read + " ContentLength: " + ESTResponse.this.contentLength); 
          if (in.available() > 0)
            throw new IOException("Stream closed with extra content in pipe that exceeds content length."); 
          in.close();
        }
      };
  }
  
  protected String readStringIncluding(char paramChar) throws IOException {
    int i;
    byte b = 0;
    do {
      i = this.inputStream.read();
      this.lineBuffer[b++] = (byte)i;
      if (b >= this.lineBuffer.length)
        throw new IOException("Server sent line > " + this.lineBuffer.length); 
    } while (i != paramChar && i > -1);
    if (i == -1)
      throw new EOFException(); 
    return (new String(this.lineBuffer, 0, b)).trim();
  }
  
  public ESTRequest getOriginalRequest() {
    return this.originalRequest;
  }
  
  public HttpUtil.Headers getHeaders() {
    return this.headers;
  }
  
  public String getHttpVersion() {
    return this.HttpVersion;
  }
  
  public int getStatusCode() {
    return this.statusCode;
  }
  
  public String getStatusMessage() {
    return this.statusMessage;
  }
  
  public InputStream getInputStream() {
    return this.inputStream;
  }
  
  public Source getSource() {
    return this.source;
  }
  
  public Long getContentLength() {
    String str = this.headers.getFirstValue("Content-Length");
    if (str == null)
      return null; 
    try {
      return Long.valueOf(Long.parseLong(str));
    } catch (RuntimeException runtimeException) {
      throw new RuntimeException("Content Length: '" + str + "' invalid. " + runtimeException.getMessage());
    } 
  }
  
  public void close() throws IOException {
    if (this.inputStream != null)
      this.inputStream.close(); 
    this.source.close();
  }
  
  private class PrintingInputStream extends InputStream {
    private final InputStream src;
    
    private PrintingInputStream(InputStream param1InputStream) {
      this.src = param1InputStream;
    }
    
    public int read() throws IOException {
      int i = this.src.read();
      System.out.print(String.valueOf((char)i));
      return i;
    }
    
    public int available() throws IOException {
      return this.src.available();
    }
    
    public void close() throws IOException {
      this.src.close();
    }
  }
}
