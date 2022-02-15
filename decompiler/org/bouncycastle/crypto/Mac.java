package org.bouncycastle.crypto;

public interface Mac {
  void init(CipherParameters paramCipherParameters) throws IllegalArgumentException;
  
  String getAlgorithmName();
  
  int getMacSize();
  
  void update(byte paramByte) throws IllegalStateException;
  
  void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalStateException;
  
  int doFinal(byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException;
  
  void reset();
}
