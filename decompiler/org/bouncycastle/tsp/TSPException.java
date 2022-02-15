package org.bouncycastle.tsp;

public class TSPException extends Exception {
  Throwable underlyingException;
  
  public TSPException(String paramString) {
    super(paramString);
  }
  
  public TSPException(String paramString, Throwable paramThrowable) {
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
