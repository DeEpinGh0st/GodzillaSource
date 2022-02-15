package org.springframework.core;

import java.io.IOException;
import org.springframework.lang.Nullable;





































public class NestedIOException
  extends IOException
{
  static {
    NestedExceptionUtils.class.getName();
  }





  
  public NestedIOException(String msg) {
    super(msg);
  }






  
  public NestedIOException(@Nullable String msg, @Nullable Throwable cause) {
    super(msg, cause);
  }






  
  @Nullable
  public String getMessage() {
    return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
  }
}
