package org.bouncycastle.cert.ocsp;

public class OCSPException extends Exception {
  private Throwable cause;
  
  public OCSPException(String paramString) {
    super(paramString);
  }
  
  public OCSPException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
