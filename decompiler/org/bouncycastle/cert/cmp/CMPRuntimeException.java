package org.bouncycastle.cert.cmp;

public class CMPRuntimeException extends RuntimeException {
  private Throwable cause;
  
  public CMPRuntimeException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
