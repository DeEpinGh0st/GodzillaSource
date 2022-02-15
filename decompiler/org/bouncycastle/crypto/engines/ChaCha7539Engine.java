package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Pack;

public class ChaCha7539Engine extends Salsa20Engine {
  public String getAlgorithmName() {
    return "ChaCha7539-" + this.rounds;
  }
  
  protected int getNonceSize() {
    return 12;
  }
  
  protected void advanceCounter(long paramLong) {
    int i = (int)(paramLong >>> 32L);
    int j = (int)paramLong;
    if (i > 0)
      throw new IllegalStateException("attempt to increase counter past 2^32."); 
    int k = this.engineState[12];
    this.engineState[12] = this.engineState[12] + j;
    if (k != 0 && this.engineState[12] < k)
      throw new IllegalStateException("attempt to increase counter past 2^32."); 
  }
  
  protected void advanceCounter() {
    this.engineState[12] = this.engineState[12] + 1;
    if (this.engineState[12] + 1 == 0)
      throw new IllegalStateException("attempt to increase counter past 2^32."); 
  }
  
  protected void retreatCounter(long paramLong) {
    int i = (int)(paramLong >>> 32L);
    int j = (int)paramLong;
    if (i != 0)
      throw new IllegalStateException("attempt to reduce counter past zero."); 
    if ((this.engineState[12] & 0xFFFFFFFFL) >= (j & 0xFFFFFFFFL)) {
      this.engineState[12] = this.engineState[12] - j;
    } else {
      throw new IllegalStateException("attempt to reduce counter past zero.");
    } 
  }
  
  protected void retreatCounter() {
    if (this.engineState[12] == 0)
      throw new IllegalStateException("attempt to reduce counter past zero."); 
    this.engineState[12] = this.engineState[12] - 1;
  }
  
  protected long getCounter() {
    return this.engineState[12] & 0xFFFFFFFFL;
  }
  
  protected void resetCounter() {
    this.engineState[12] = 0;
  }
  
  protected void setKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 != null) {
      if (paramArrayOfbyte1.length != 32)
        throw new IllegalArgumentException(getAlgorithmName() + " requires 256 bit key"); 
      packTauOrSigma(paramArrayOfbyte1.length, this.engineState, 0);
      Pack.littleEndianToInt(paramArrayOfbyte1, 0, this.engineState, 4, 8);
    } 
    Pack.littleEndianToInt(paramArrayOfbyte2, 0, this.engineState, 13, 3);
  }
  
  protected void generateKeyStream(byte[] paramArrayOfbyte) {
    ChaChaEngine.chachaCore(this.rounds, this.engineState, this.x);
    Pack.intToLittleEndian(this.x, paramArrayOfbyte, 0);
  }
}
