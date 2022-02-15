package org.springframework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import org.springframework.lang.Nullable;



































public abstract class StreamUtils
{
  public static final int BUFFER_SIZE = 4096;
  private static final byte[] EMPTY_CONTENT = new byte[0];








  
  public static byte[] copyToByteArray(@Nullable InputStream in) throws IOException {
    if (in == null) {
      return new byte[0];
    }
    
    ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
    copy(in, out);
    return out.toByteArray();
  }








  
  public static String copyToString(@Nullable InputStream in, Charset charset) throws IOException {
    if (in == null) {
      return "";
    }
    
    StringBuilder out = new StringBuilder(4096);
    InputStreamReader reader = new InputStreamReader(in, charset);
    char[] buffer = new char[4096];
    int charsRead;
    while ((charsRead = reader.read(buffer)) != -1) {
      out.append(buffer, 0, charsRead);
    }
    return out.toString();
  }








  
  public static String copyToString(ByteArrayOutputStream baos, Charset charset) {
    Assert.notNull(baos, "No ByteArrayOutputStream specified");
    Assert.notNull(charset, "No Charset specified");
    
    try {
      return baos.toString(charset.name());
    }
    catch (UnsupportedEncodingException ex) {
      
      throw new IllegalArgumentException("Invalid charset name: " + charset, ex);
    } 
  }







  
  public static void copy(byte[] in, OutputStream out) throws IOException {
    Assert.notNull(in, "No input byte array specified");
    Assert.notNull(out, "No OutputStream specified");
    
    out.write(in);
    out.flush();
  }








  
  public static void copy(String in, Charset charset, OutputStream out) throws IOException {
    Assert.notNull(in, "No input String specified");
    Assert.notNull(charset, "No Charset specified");
    Assert.notNull(out, "No OutputStream specified");
    
    Writer writer = new OutputStreamWriter(out, charset);
    writer.write(in);
    writer.flush();
  }








  
  public static int copy(InputStream in, OutputStream out) throws IOException {
    Assert.notNull(in, "No InputStream specified");
    Assert.notNull(out, "No OutputStream specified");
    
    int byteCount = 0;
    byte[] buffer = new byte[4096];
    int bytesRead;
    while ((bytesRead = in.read(buffer)) != -1) {
      out.write(buffer, 0, bytesRead);
      byteCount += bytesRead;
    } 
    out.flush();
    return byteCount;
  }













  
  public static long copyRange(InputStream in, OutputStream out, long start, long end) throws IOException {
    Assert.notNull(in, "No InputStream specified");
    Assert.notNull(out, "No OutputStream specified");
    
    long skipped = in.skip(start);
    if (skipped < start) {
      throw new IOException("Skipped only " + skipped + " bytes out of " + start + " required");
    }
    
    long bytesToCopy = end - start + 1L;
    byte[] buffer = new byte[(int)Math.min(4096L, bytesToCopy)];
    while (bytesToCopy > 0L) {
      int bytesRead = in.read(buffer);
      if (bytesRead == -1) {
        break;
      }
      if (bytesRead <= bytesToCopy) {
        out.write(buffer, 0, bytesRead);
        bytesToCopy -= bytesRead;
        continue;
      } 
      out.write(buffer, 0, (int)bytesToCopy);
      bytesToCopy = 0L;
    } 
    
    return end - start + 1L - bytesToCopy;
  }








  
  public static int drain(InputStream in) throws IOException {
    Assert.notNull(in, "No InputStream specified");
    byte[] buffer = new byte[4096];
    int bytesRead = -1;
    int byteCount = 0;
    while ((bytesRead = in.read(buffer)) != -1) {
      byteCount += bytesRead;
    }
    return byteCount;
  }





  
  public static InputStream emptyInput() {
    return new ByteArrayInputStream(EMPTY_CONTENT);
  }






  
  public static InputStream nonClosing(InputStream in) {
    Assert.notNull(in, "No InputStream specified");
    return new NonClosingInputStream(in);
  }






  
  public static OutputStream nonClosing(OutputStream out) {
    Assert.notNull(out, "No OutputStream specified");
    return new NonClosingOutputStream(out);
  }
  
  private static class NonClosingInputStream
    extends FilterInputStream
  {
    public NonClosingInputStream(InputStream in) {
      super(in);
    }

    
    public void close() throws IOException {}
  }

  
  private static class NonClosingOutputStream
    extends FilterOutputStream
  {
    public NonClosingOutputStream(OutputStream out) {
      super(out);
    }


    
    public void write(byte[] b, int off, int let) throws IOException {
      this.out.write(b, off, let);
    }
    
    public void close() throws IOException {}
  }
}
