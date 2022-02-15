package org.bouncycastle.pkcs;

public class PKCSException extends Exception {
  private Throwable cause;
  
  public PKCSException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public PKCSException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
