package org.bouncycastle.crypto.prng.drbg;

public interface SP80090DRBG {
  int getBlockSize();
  
  int generate(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, boolean paramBoolean);
  
  void reseed(byte[] paramArrayOfbyte);
}
