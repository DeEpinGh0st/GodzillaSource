package org.bouncycastle.crypto;

public interface Wrapper {
  void init(boolean paramBoolean, CipherParameters paramCipherParameters);
  
  String getAlgorithmName();
  
  byte[] wrap(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  byte[] unwrap(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException;
}
