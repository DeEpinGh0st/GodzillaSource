package org.bouncycastle.eac;

import java.io.IOException;

public class EACIOException extends IOException {
  private Throwable cause;
  
  public EACIOException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public EACIOException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
