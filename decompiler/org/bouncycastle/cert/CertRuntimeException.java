package org.bouncycastle.cert;

public class CertRuntimeException extends RuntimeException {
  private Throwable cause;
  
  public CertRuntimeException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
