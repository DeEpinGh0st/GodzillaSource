package org.bouncycastle.cert.dane;

public class DANEException extends Exception {
  private Throwable cause;
  
  public DANEException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public DANEException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
