package org.bouncycastle.cert.crmf;

public class CRMFException extends Exception {
  private Throwable cause;
  
  public CRMFException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
