package org.bouncycastle.crypto.prng;

public class ReversedWindowGenerator implements RandomGenerator {
  private final RandomGenerator generator;
  
  private byte[] window;
  
  private int windowCount;
  
  public ReversedWindowGenerator(RandomGenerator paramRandomGenerator, int paramInt) {
    if (paramRandomGenerator == null)
      throw new IllegalArgumentException("generator cannot be null"); 
    if (paramInt < 2)
      throw new IllegalArgumentException("windowSize must be at least 2"); 
    this.generator = paramRandomGenerator;
    this.window = new byte[paramInt];
  }
  
  public void addSeedMaterial(byte[] paramArrayOfbyte) {
    synchronized (this) {
      this.windowCount = 0;
      this.generator.addSeedMaterial(paramArrayOfbyte);
    } 
  }
  
  public void addSeedMaterial(long paramLong) {
    synchronized (this) {
      this.windowCount = 0;
      this.generator.addSeedMaterial(paramLong);
    } 
  }
  
  public void nextBytes(byte[] paramArrayOfbyte) {
    doNextBytes(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public void nextBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    doNextBytes(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  private void doNextBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    synchronized (this) {
      byte b = 0;
      while (b < paramInt2) {
        if (this.windowCount < 1) {
          this.generator.nextBytes(this.window, 0, this.window.length);
          this.windowCount = this.window.length;
        } 
        paramArrayOfbyte[paramInt1 + b++] = this.window[--this.windowCount];
      } 
    } 
  }
}
