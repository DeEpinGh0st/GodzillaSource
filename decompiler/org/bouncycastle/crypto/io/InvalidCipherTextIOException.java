package org.bouncycastle.crypto.io;

public class InvalidCipherTextIOException extends CipherIOException {
  private static final long serialVersionUID = 1L;
  
  public InvalidCipherTextIOException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
}
