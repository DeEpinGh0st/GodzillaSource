package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;


















@GwtIncompatible
public class UncheckedTimeoutException
  extends RuntimeException
{
  private static final long serialVersionUID = 0L;
  
  public UncheckedTimeoutException() {}
  
  public UncheckedTimeoutException(String message) {
    super(message);
  }
  
  public UncheckedTimeoutException(Throwable cause) {
    super(cause);
  }
  
  public UncheckedTimeoutException(String message, Throwable cause) {
    super(message, cause);
  }
}
