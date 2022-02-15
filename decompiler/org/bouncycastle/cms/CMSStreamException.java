package org.bouncycastle.cms;

import java.io.IOException;

public class CMSStreamException extends IOException {
  private final Throwable underlying = null;
  
  CMSStreamException(String paramString) {
    super(paramString);
  }
  
  CMSStreamException(String paramString, Throwable paramThrowable) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.underlying;
  }
}
