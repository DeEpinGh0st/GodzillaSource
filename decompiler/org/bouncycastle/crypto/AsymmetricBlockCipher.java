package org.bouncycastle.crypto;

public interface AsymmetricBlockCipher {
  void init(boolean paramBoolean, CipherParameters paramCipherParameters);
  
  int getInputBlockSize();
  
  int getOutputBlockSize();
  
  byte[] processBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException;
}
