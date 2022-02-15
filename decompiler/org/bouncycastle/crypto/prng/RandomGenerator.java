package org.bouncycastle.crypto.prng;

public interface RandomGenerator {
  void addSeedMaterial(byte[] paramArrayOfbyte);
  
  void addSeedMaterial(long paramLong);
  
  void nextBytes(byte[] paramArrayOfbyte);
  
  void nextBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
}
