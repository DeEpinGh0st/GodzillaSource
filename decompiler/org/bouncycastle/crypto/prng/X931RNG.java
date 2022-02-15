package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.BlockCipher;

public class X931RNG {
  private static final long BLOCK64_RESEED_MAX = 32768L;
  
  private static final long BLOCK128_RESEED_MAX = 8388608L;
  
  private static final int BLOCK64_MAX_BITS_REQUEST = 4096;
  
  private static final int BLOCK128_MAX_BITS_REQUEST = 262144;
  
  private final BlockCipher engine;
  
  private final EntropySource entropySource;
  
  private final byte[] DT;
  
  private final byte[] I;
  
  private final byte[] R;
  
  private byte[] V;
  
  private long reseedCounter = 1L;
  
  public X931RNG(BlockCipher paramBlockCipher, byte[] paramArrayOfbyte, EntropySource paramEntropySource) {
    this.engine = paramBlockCipher;
    this.entropySource = paramEntropySource;
    this.DT = new byte[paramBlockCipher.getBlockSize()];
    System.arraycopy(paramArrayOfbyte, 0, this.DT, 0, this.DT.length);
    this.I = new byte[paramBlockCipher.getBlockSize()];
    this.R = new byte[paramBlockCipher.getBlockSize()];
  }
  
  int generate(byte[] paramArrayOfbyte, boolean paramBoolean) {
    if (this.R.length == 8) {
      if (this.reseedCounter > 32768L)
        return -1; 
      if (isTooLarge(paramArrayOfbyte, 512))
        throw new IllegalArgumentException("Number of bits per request limited to 4096"); 
    } else {
      if (this.reseedCounter > 8388608L)
        return -1; 
      if (isTooLarge(paramArrayOfbyte, 32768))
        throw new IllegalArgumentException("Number of bits per request limited to 262144"); 
    } 
    if (paramBoolean || this.V == null) {
      this.V = this.entropySource.getEntropy();
      if (this.V.length != this.engine.getBlockSize())
        throw new IllegalStateException("Insufficient entropy returned"); 
    } 
    int i = paramArrayOfbyte.length / this.R.length;
    int j;
    for (j = 0; j < i; j++) {
      this.engine.processBlock(this.DT, 0, this.I, 0);
      process(this.R, this.I, this.V);
      process(this.V, this.R, this.I);
      System.arraycopy(this.R, 0, paramArrayOfbyte, j * this.R.length, this.R.length);
      increment(this.DT);
    } 
    j = paramArrayOfbyte.length - i * this.R.length;
    if (j > 0) {
      this.engine.processBlock(this.DT, 0, this.I, 0);
      process(this.R, this.I, this.V);
      process(this.V, this.R, this.I);
      System.arraycopy(this.R, 0, paramArrayOfbyte, i * this.R.length, j);
      increment(this.DT);
    } 
    this.reseedCounter++;
    return paramArrayOfbyte.length;
  }
  
  void reseed() {
    this.V = this.entropySource.getEntropy();
    if (this.V.length != this.engine.getBlockSize())
      throw new IllegalStateException("Insufficient entropy returned"); 
    this.reseedCounter = 1L;
  }
  
  EntropySource getEntropySource() {
    return this.entropySource;
  }
  
  private void process(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    for (byte b = 0; b != paramArrayOfbyte1.length; b++)
      paramArrayOfbyte1[b] = (byte)(paramArrayOfbyte2[b] ^ paramArrayOfbyte3[b]); 
    this.engine.processBlock(paramArrayOfbyte1, 0, paramArrayOfbyte1, 0);
  }
  
  private void increment(byte[] paramArrayOfbyte) {
    int i = paramArrayOfbyte.length - 1;
    paramArrayOfbyte[i] = (byte)(paramArrayOfbyte[i] + 1);
    while (i >= 0 && (byte)(paramArrayOfbyte[i] + 1) == 0)
      i--; 
  }
  
  private static boolean isTooLarge(byte[] paramArrayOfbyte, int paramInt) {
    return (paramArrayOfbyte != null && paramArrayOfbyte.length > paramInt);
  }
}
