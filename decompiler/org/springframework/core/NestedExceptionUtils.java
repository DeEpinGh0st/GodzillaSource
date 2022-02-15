package org.springframework.core;

import org.springframework.lang.Nullable;





































public abstract class NestedExceptionUtils
{
  @Nullable
  public static String buildMessage(@Nullable String message, @Nullable Throwable cause) {
    if (cause == null) {
      return message;
    }
    StringBuilder sb = new StringBuilder(64);
    if (message != null) {
      sb.append(message).append("; ");
    }
    sb.append("nested exception is ").append(cause);
    return sb.toString();
  }






  
  @Nullable
  public static Throwable getRootCause(@Nullable Throwable original) {
    if (original == null) {
      return null;
    }
    Throwable rootCause = null;
    Throwable cause = original.getCause();
    while (cause != null && cause != rootCause) {
      rootCause = cause;
      cause = cause.getCause();
    } 
    return rootCause;
  }









  
  public static Throwable getMostSpecificCause(Throwable original) {
    Throwable rootCause = getRootCause(original);
    return (rootCause != null) ? rootCause : original;
  }
}
