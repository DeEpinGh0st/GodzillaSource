package org.bouncycastle.util.io.pem;

import java.io.IOException;

public class PemGenerationException extends IOException {
  private Throwable cause;
  
  public PemGenerationException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public PemGenerationException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
