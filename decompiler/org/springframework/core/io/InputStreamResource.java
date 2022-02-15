package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;












































public class InputStreamResource
  extends AbstractResource
{
  private final InputStream inputStream;
  private final String description;
  private boolean read = false;
  
  public InputStreamResource(InputStream inputStream) {
    this(inputStream, "resource loaded through InputStream");
  }





  
  public InputStreamResource(InputStream inputStream, @Nullable String description) {
    Assert.notNull(inputStream, "InputStream must not be null");
    this.inputStream = inputStream;
    this.description = (description != null) ? description : "";
  }





  
  public boolean exists() {
    return true;
  }




  
  public boolean isOpen() {
    return true;
  }





  
  public InputStream getInputStream() throws IOException, IllegalStateException {
    if (this.read) {
      throw new IllegalStateException("InputStream has already been read - do not use InputStreamResource if a stream needs to be read multiple times");
    }
    
    this.read = true;
    return this.inputStream;
  }





  
  public String getDescription() {
    return "InputStream resource [" + this.description + "]";
  }





  
  public boolean equals(@Nullable Object other) {
    return (this == other || (other instanceof InputStreamResource && ((InputStreamResource)other).inputStream
      .equals(this.inputStream)));
  }




  
  public int hashCode() {
    return this.inputStream.hashCode();
  }
}
