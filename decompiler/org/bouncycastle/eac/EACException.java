package org.bouncycastle.eac;

public class EACException extends Exception {
  private Throwable cause;
  
  public EACException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public EACException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
