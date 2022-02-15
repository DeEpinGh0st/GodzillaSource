package org.bouncycastle.tsp;

import java.io.IOException;

public class TSPIOException extends IOException {
  Throwable underlyingException;
  
  public TSPIOException(String paramString) {
    super(paramString);
  }
  
  public TSPIOException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.underlyingException = paramThrowable;
  }
  
  public Exception getUnderlyingException() {
    return (Exception)this.underlyingException;
  }
  
  public Throwable getCause() {
    return this.underlyingException;
  }
}
