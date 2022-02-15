package org.bouncycastle.util.encoders;

public class EncoderException extends IllegalStateException {
  private Throwable cause;
  
  EncoderException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
