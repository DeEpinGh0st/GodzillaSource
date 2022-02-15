package org.bouncycastle.crypto;

public interface BlockCipher {
  void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException;
  
  String getAlgorithmName();
  
  int getBlockSize();
  
  int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException;
  
  void reset();
}
