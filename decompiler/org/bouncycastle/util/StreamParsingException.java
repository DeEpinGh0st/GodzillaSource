package org.bouncycastle.util;

public class StreamParsingException extends Exception {
  Throwable _e;
  
  public StreamParsingException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this._e = paramThrowable;
  }
  
  public Throwable getCause() {
    return this._e;
  }
}
