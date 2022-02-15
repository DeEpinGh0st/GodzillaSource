package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.util.Arrays;

public class DSTU7624Mac implements Mac {
  private static final int BITS_IN_BYTE = 8;
  
  private byte[] buf;
  
  private int bufOff;
  
  private int macSize;
  
  private int blockSize;
  
  private DSTU7624Engine engine;
  
  private byte[] c;
  
  private byte[] cTemp;
  
  private byte[] kDelta;
  
  public DSTU7624Mac(int paramInt1, int paramInt2) {
    this.engine = new DSTU7624Engine(paramInt1);
    this.blockSize = paramInt1 / 8;
    this.macSize = paramInt2 / 8;
    this.c = new byte[this.blockSize];
    this.kDelta = new byte[this.blockSize];
    this.cTemp = new byte[this.blockSize];
    this.buf = new byte[this.blockSize];
  }
  
  public void init(CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (paramCipherParameters instanceof org.bouncycastle.crypto.params.KeyParameter) {
      this.engine.init(true, paramCipherParameters);
      this.engine.processBlock(this.kDelta, 0, this.kDelta, 0);
    } else {
      throw new IllegalArgumentException("Invalid parameter passed to DSTU7624Mac");
    } 
  }
  
  public String getAlgorithmName() {
    return "DSTU7624Mac";
  }
  
  public int getMacSize() {
    return this.macSize;
  }
  
  public void update(byte paramByte) {
    if (this.bufOff == this.buf.length) {
      processBlock(this.buf, 0);
      this.bufOff = 0;
    } 
    this.buf[this.bufOff++] = paramByte;
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt2 < 0)
      throw new IllegalArgumentException("can't have a negative input length!"); 
    int i = this.engine.getBlockSize();
    int j = i - this.bufOff;
    if (paramInt2 > j) {
      System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, j);
      processBlock(this.buf, 0);
      this.bufOff = 0;
      paramInt2 -= j;
      for (paramInt1 += j; paramInt2 > i; paramInt1 += i) {
        processBlock(paramArrayOfbyte, paramInt1);
        paramInt2 -= i;
      } 
    } 
    System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, paramInt2);
    this.bufOff += paramInt2;
  }
  
  private void processBlock(byte[] paramArrayOfbyte, int paramInt) {
    xor(this.c, 0, paramArrayOfbyte, paramInt, this.cTemp);
    this.engine.processBlock(this.cTemp, 0, this.c, 0);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException {
    if (this.bufOff % this.buf.length != 0)
      throw new DataLengthException("input must be a multiple of blocksize"); 
    xor(this.c, 0, this.buf, 0, this.cTemp);
    xor(this.cTemp, 0, this.kDelta, 0, this.c);
    this.engine.processBlock(this.c, 0, this.c, 0);
    if (this.macSize + paramInt > paramArrayOfbyte.length)
      throw new OutputLengthException("output buffer too short"); 
    System.arraycopy(this.c, 0, paramArrayOfbyte, paramInt, this.macSize);
    return this.macSize;
  }
  
  public void reset() {
    Arrays.fill(this.c, (byte)0);
    Arrays.fill(this.cTemp, (byte)0);
    Arrays.fill(this.kDelta, (byte)0);
    Arrays.fill(this.buf, (byte)0);
    this.engine.reset();
    this.engine.processBlock(this.kDelta, 0, this.kDelta, 0);
    this.bufOff = 0;
  }
  
  private void xor(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, byte[] paramArrayOfbyte3) {
    if (paramArrayOfbyte1.length - paramInt1 < this.blockSize || paramArrayOfbyte2.length - paramInt2 < this.blockSize || paramArrayOfbyte3.length < this.blockSize)
      throw new IllegalArgumentException("some of input buffers too short"); 
    for (byte b = 0; b < this.blockSize; b++)
      paramArrayOfbyte3[b] = (byte)(paramArrayOfbyte1[b + paramInt1] ^ paramArrayOfbyte2[b + paramInt2]); 
  }
}
