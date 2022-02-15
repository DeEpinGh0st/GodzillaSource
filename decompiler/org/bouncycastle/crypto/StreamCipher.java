package org.bouncycastle.crypto;

public interface StreamCipher {
  void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException;
  
  String getAlgorithmName();
  
  byte returnByte(byte paramByte);
  
  int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws DataLengthException;
  
  void reset();
}
