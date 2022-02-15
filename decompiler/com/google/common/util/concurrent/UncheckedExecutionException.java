package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;





























@GwtCompatible
public class UncheckedExecutionException
  extends RuntimeException
{
  private static final long serialVersionUID = 0L;
  
  protected UncheckedExecutionException() {}
  
  protected UncheckedExecutionException(String message) {
    super(message);
  }

  
  public UncheckedExecutionException(String message, Throwable cause) {
    super(message, cause);
  }

  
  public UncheckedExecutionException(Throwable cause) {
    super(cause);
  }
}
