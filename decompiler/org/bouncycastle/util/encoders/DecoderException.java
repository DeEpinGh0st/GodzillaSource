package org.bouncycastle.util.encoders;

public class DecoderException extends IllegalStateException {
  private Throwable cause;
  
  DecoderException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
