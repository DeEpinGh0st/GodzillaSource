package org.bouncycastle.crypto;

public interface Digest {
  String getAlgorithmName();
  
  int getDigestSize();
  
  void update(byte paramByte);
  
  void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  int doFinal(byte[] paramArrayOfbyte, int paramInt);
  
  void reset();
}
