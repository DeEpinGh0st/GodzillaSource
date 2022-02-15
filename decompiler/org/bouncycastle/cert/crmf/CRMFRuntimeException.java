package org.bouncycastle.cert.crmf;

public class CRMFRuntimeException extends RuntimeException {
  private Throwable cause;
  
  public CRMFRuntimeException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
