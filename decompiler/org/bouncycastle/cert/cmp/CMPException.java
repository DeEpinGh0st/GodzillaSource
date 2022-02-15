package org.bouncycastle.cert.cmp;

public class CMPException extends Exception {
  private Throwable cause;
  
  public CMPException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public CMPException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
