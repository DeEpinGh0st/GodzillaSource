package org.springframework.core;

import org.springframework.lang.Nullable;





































public abstract class NestedRuntimeException
  extends RuntimeException
{
  private static final long serialVersionUID = 5439915454935047936L;
  
  static {
    NestedExceptionUtils.class.getName();
  }





  
  public NestedRuntimeException(String msg) {
    super(msg);
  }






  
  public NestedRuntimeException(@Nullable String msg, @Nullable Throwable cause) {
    super(msg, cause);
  }






  
  @Nullable
  public String getMessage() {
    return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
  }






  
  @Nullable
  public Throwable getRootCause() {
    return NestedExceptionUtils.getRootCause(this);
  }








  
  public Throwable getMostSpecificCause() {
    Throwable rootCause = getRootCause();
    return (rootCause != null) ? rootCause : this;
  }







  
  public boolean contains(@Nullable Class<?> exType) {
    if (exType == null) {
      return false;
    }
    if (exType.isInstance(this)) {
      return true;
    }
    Throwable cause = getCause();
    if (cause == this) {
      return false;
    }
    if (cause instanceof NestedRuntimeException) {
      return ((NestedRuntimeException)cause).contains(exType);
    }
    
    while (cause != null) {
      if (exType.isInstance(cause)) {
        return true;
      }
      if (cause.getCause() == cause) {
        break;
      }
      cause = cause.getCause();
    } 
    return false;
  }
}
