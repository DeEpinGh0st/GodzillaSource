package org.bouncycastle.cmc;

public class CMCException extends Exception {
  private final Throwable cause;
  
  public CMCException(String paramString) {
    this(paramString, null);
  }
  
  public CMCException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
