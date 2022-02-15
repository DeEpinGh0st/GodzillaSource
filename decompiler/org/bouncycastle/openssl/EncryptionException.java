package org.bouncycastle.openssl;

public class EncryptionException extends PEMException {
  private Throwable cause;
  
  public EncryptionException(String paramString) {
    super(paramString);
  }
  
  public EncryptionException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}
