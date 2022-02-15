package org.bouncycastle.util;

public class StoreException extends RuntimeException {
  private Throwable _e;
  
  public StoreException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this._e = paramThrowable;
  }
  
  public Throwable getCause() {
    return this._e;
  }
}
