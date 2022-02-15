package org.bouncycastle.pkcs;

import java.io.IOException;

public class PKCSIOException extends IOException {
  private Throwable cause;
  
  public PKCSIOException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public PKCSIOException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
