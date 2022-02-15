package org.bouncycastle.crypto;

public class InvalidCipherTextException extends CryptoException {
  public InvalidCipherTextException() {}
  
  public InvalidCipherTextException(String paramString) {
    super(paramString);
  }
  
  public InvalidCipherTextException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
}
