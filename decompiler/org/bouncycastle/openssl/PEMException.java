package org.bouncycastle.openssl;

import java.io.IOException;

public class PEMException extends IOException {
  Exception underlying;
  
  public PEMException(String paramString) {
    super(paramString);
  }
  
  public PEMException(String paramString, Exception paramException) {
    super(paramString);
    this.underlying = paramException;
  }
  
  public Exception getUnderlyingException() {
    return this.underlying;
  }
  
  public Throwable getCause() {
    return this.underlying;
  }
}
