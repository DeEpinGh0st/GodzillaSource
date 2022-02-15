package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.SkippingStreamCipher;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class SICBlockCipher extends StreamBlockCipher implements SkippingStreamCipher {
  private final BlockCipher cipher;
  
  private final int blockSize;
  
  private byte[] IV;
  
  private byte[] counter;
  
  private byte[] counterOut;
  
  private int byteCount;
  
  public SICBlockCipher(BlockCipher paramBlockCipher) {
    super(paramBlockCipher);
    this.cipher = paramBlockCipher;
    this.blockSize = this.cipher.getBlockSize();
    this.IV = new byte[this.blockSize];
    this.counter = new byte[this.blockSize];
    this.counterOut = new byte[this.blockSize];
    this.byteCount = 0;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (paramCipherParameters instanceof ParametersWithIV) {
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      this.IV = Arrays.clone(parametersWithIV.getIV());
      if (this.blockSize < this.IV.length)
        throw new IllegalArgumentException("CTR/SIC mode requires IV no greater than: " + this.blockSize + " bytes."); 
      byte b = (8 > this.blockSize / 2) ? (this.blockSize / 2) : 8;
      if (this.blockSize - this.IV.length > b)
        throw new IllegalArgumentException("CTR/SIC mode requires IV of at least: " + (this.blockSize - b) + " bytes."); 
      if (parametersWithIV.getParameters() != null)
        this.cipher.init(true, parametersWithIV.getParameters()); 
      reset();
    } else {
      throw new IllegalArgumentException("CTR/SIC mode requires ParametersWithIV");
    } 
  }
  
  public String getAlgorithmName() {
    return this.cipher.getAlgorithmName() + "/SIC";
  }
  
  public int getBlockSize() {
    return this.cipher.getBlockSize();
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    processBytes(paramArrayOfbyte1, paramInt1, this.blockSize, paramArrayOfbyte2, paramInt2);
    return this.blockSize;
  }
  
  protected byte calculateByte(byte paramByte) throws DataLengthException, IllegalStateException {
    if (this.byteCount == 0) {
      this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
      return (byte)(this.counterOut[this.byteCount++] ^ paramByte);
    } 
    byte b = (byte)(this.counterOut[this.byteCount++] ^ paramByte);
    if (this.byteCount == this.counter.length) {
      this.byteCount = 0;
      incrementCounterAt(0);
      checkCounter();
    } 
    return b;
  }
  
  private void checkCounter() {
    if (this.IV.length < this.blockSize)
      for (byte b = 0; b != this.IV.length; b++) {
        if (this.counter[b] != this.IV[b])
          throw new IllegalStateException("Counter in CTR/SIC mode out of range."); 
      }  
  }
  
  private void incrementCounterAt(int paramInt) {
    int i = this.counter.length - paramInt;
    do {
      this.counter[i] = (byte)(this.counter[i] + 1);
    } while (--i >= 0 && (byte)(this.counter[i] + 1) == 0);
  }
  
  private void incrementCounter(int paramInt) {
    byte b = this.counter[this.counter.length - 1];
    this.counter[this.counter.length - 1] = (byte)(this.counter[this.counter.length - 1] + paramInt);
    if (b != 0 && this.counter[this.counter.length - 1] < b)
      incrementCounterAt(1); 
  }
  
  private void decrementCounterAt(int paramInt) {
    int i = this.counter.length - paramInt;
    while (--i >= 0) {
      this.counter[i] = (byte)(this.counter[i] - 1);
      if ((byte)(this.counter[i] - 1) != -1)
        return; 
    } 
  }
  
  private void adjustCounter(long paramLong) {
    if (paramLong >= 0L) {
      long l1 = (paramLong + this.byteCount) / this.blockSize;
      long l2 = l1;
      if (l2 > 255L)
        for (byte b = 5; b >= 1; b--) {
          long l = 1L << 8 * b;
          while (l2 >= l) {
            incrementCounterAt(b);
            l2 -= l;
          } 
        }  
      incrementCounter((int)l2);
      this.byteCount = (int)(paramLong + this.byteCount - this.blockSize * l1);
    } else {
      long l1 = (-paramLong - this.byteCount) / this.blockSize;
      long l2 = l1;
      if (l2 > 255L)
        for (byte b = 5; b >= 1; b--) {
          long l = 1L << 8 * b;
          while (l2 > l) {
            decrementCounterAt(b);
            l2 -= l;
          } 
        }  
      long l3;
      for (l3 = 0L; l3 != l2; l3++)
        decrementCounterAt(0); 
      int i = (int)(this.byteCount + paramLong + this.blockSize * l1);
      if (i >= 0) {
        this.byteCount = 0;
      } else {
        decrementCounterAt(0);
        this.byteCount = this.blockSize + i;
      } 
    } 
  }
  
  public void reset() {
    Arrays.fill(this.counter, (byte)0);
    System.arraycopy(this.IV, 0, this.counter, 0, this.IV.length);
    this.cipher.reset();
    this.byteCount = 0;
  }
  
  public long skip(long paramLong) {
    adjustCounter(paramLong);
    checkCounter();
    this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
    return paramLong;
  }
  
  public long seekTo(long paramLong) {
    reset();
    return skip(paramLong);
  }
  
  public long getPosition() {
    byte[] arrayOfByte = new byte[this.counter.length];
    System.arraycopy(this.counter, 0, arrayOfByte, 0, arrayOfByte.length);
    for (int i = arrayOfByte.length - 1; i >= 1; i--) {
      int j;
      if (i < this.IV.length) {
        j = (arrayOfByte[i] & 0xFF) - (this.IV[i] & 0xFF);
      } else {
        j = arrayOfByte[i] & 0xFF;
      } 
      if (j < 0) {
        arrayOfByte[i - 1] = (byte)(arrayOfByte[i - 1] - 1);
        j += 256;
      } 
      arrayOfByte[i] = (byte)j;
    } 
    return Pack.bigEndianToLong(arrayOfByte, arrayOfByte.length - 8) * this.blockSize + this.byteCount;
  }
}
