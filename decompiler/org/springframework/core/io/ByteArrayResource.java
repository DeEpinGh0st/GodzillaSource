package org.springframework.core.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;







































public class ByteArrayResource
  extends AbstractResource
{
  private final byte[] byteArray;
  private final String description;
  
  public ByteArrayResource(byte[] byteArray) {
    this(byteArray, "resource loaded from byte array");
  }





  
  public ByteArrayResource(byte[] byteArray, @Nullable String description) {
    Assert.notNull(byteArray, "Byte array must not be null");
    this.byteArray = byteArray;
    this.description = (description != null) ? description : "";
  }




  
  public final byte[] getByteArray() {
    return this.byteArray;
  }




  
  public boolean exists() {
    return true;
  }




  
  public long contentLength() {
    return this.byteArray.length;
  }






  
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(this.byteArray);
  }





  
  public String getDescription() {
    return "Byte array resource [" + this.description + "]";
  }






  
  public boolean equals(@Nullable Object other) {
    return (this == other || (other instanceof ByteArrayResource && 
      Arrays.equals(((ByteArrayResource)other).byteArray, this.byteArray)));
  }





  
  public int hashCode() {
    return Arrays.hashCode(this.byteArray);
  }
}
