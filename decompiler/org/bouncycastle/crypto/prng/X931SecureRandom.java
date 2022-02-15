package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;

public class X931SecureRandom extends SecureRandom {
  private final boolean predictionResistant;
  
  private final SecureRandom randomSource;
  
  private final X931RNG drbg;
  
  X931SecureRandom(SecureRandom paramSecureRandom, X931RNG paramX931RNG, boolean paramBoolean) {
    this.randomSource = paramSecureRandom;
    this.drbg = paramX931RNG;
    this.predictionResistant = paramBoolean;
  }
  
  public void setSeed(byte[] paramArrayOfbyte) {
    synchronized (this) {
      if (this.randomSource != null)
        this.randomSource.setSeed(paramArrayOfbyte); 
    } 
  }
  
  public void setSeed(long paramLong) {
    synchronized (this) {
      if (this.randomSource != null)
        this.randomSource.setSeed(paramLong); 
    } 
  }
  
  public void nextBytes(byte[] paramArrayOfbyte) {
    synchronized (this) {
      if (this.drbg.generate(paramArrayOfbyte, this.predictionResistant) < 0) {
        this.drbg.reseed();
        this.drbg.generate(paramArrayOfbyte, this.predictionResistant);
      } 
    } 
  }
  
  public byte[] generateSeed(int paramInt) {
    return EntropyUtil.generateSeed(this.drbg.getEntropySource(), paramInt);
  }
}
