package org.springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.lang.Nullable;
































public class DescriptiveResource
  extends AbstractResource
{
  private final String description;
  
  public DescriptiveResource(@Nullable String description) {
    this.description = (description != null) ? description : "";
  }


  
  public boolean exists() {
    return false;
  }

  
  public boolean isReadable() {
    return false;
  }

  
  public InputStream getInputStream() throws IOException {
    throw new FileNotFoundException(
        getDescription() + " cannot be opened because it does not point to a readable resource");
  }

  
  public String getDescription() {
    return this.description;
  }





  
  public boolean equals(@Nullable Object other) {
    return (this == other || (other instanceof DescriptiveResource && ((DescriptiveResource)other).description
      .equals(this.description)));
  }




  
  public int hashCode() {
    return this.description.hashCode();
  }
}
