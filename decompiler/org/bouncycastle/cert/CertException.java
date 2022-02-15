package org.bouncycastle.cert;

public class CertException extends Exception {
  private Throwable cause;
  
  public CertException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public CertException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
