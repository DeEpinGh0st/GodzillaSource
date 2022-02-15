package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public abstract class GeneralDigest implements ExtendedDigest, Memoable {
  private static final int BYTE_LENGTH = 64;
  
  private final byte[] xBuf = new byte[4];
  
  private int xBufOff;
  
  private long byteCount;
  
  protected GeneralDigest() {
    this.xBufOff = 0;
  }
  
  protected GeneralDigest(GeneralDigest paramGeneralDigest) {
    copyIn(paramGeneralDigest);
  }
  
  protected GeneralDigest(byte[] paramArrayOfbyte) {
    System.arraycopy(paramArrayOfbyte, 0, this.xBuf, 0, this.xBuf.length);
    this.xBufOff = Pack.bigEndianToInt(paramArrayOfbyte, 4);
    this.byteCount = Pack.bigEndianToLong(paramArrayOfbyte, 8);
  }
  
  protected void copyIn(GeneralDigest paramGeneralDigest) {
    System.arraycopy(paramGeneralDigest.xBuf, 0, this.xBuf, 0, paramGeneralDigest.xBuf.length);
    this.xBufOff = paramGeneralDigest.xBufOff;
    this.byteCount = paramGeneralDigest.byteCount;
  }
  
  public void update(byte paramByte) {
    this.xBuf[this.xBufOff++] = paramByte;
    if (this.xBufOff == this.xBuf.length) {
      processWord(this.xBuf, 0);
      this.xBufOff = 0;
    } 
    this.byteCount++;
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    paramInt2 = Math.max(0, paramInt2);
    byte b = 0;
    if (this.xBufOff != 0)
      while (b < paramInt2) {
        this.xBuf[this.xBufOff++] = paramArrayOfbyte[paramInt1 + b++];
        if (this.xBufOff == 4) {
          processWord(this.xBuf, 0);
          this.xBufOff = 0;
          break;
        } 
      }  
    int i = (paramInt2 - b & 0xFFFFFFFC) + b;
    while (b < i) {
      processWord(paramArrayOfbyte, paramInt1 + b);
      b += 4;
    } 
    while (b < paramInt2)
      this.xBuf[this.xBufOff++] = paramArrayOfbyte[paramInt1 + b++]; 
    this.byteCount += paramInt2;
  }
  
  public void finish() {
    long l = this.byteCount << 3L;
    update(-128);
    while (this.xBufOff != 0)
      update((byte)0); 
    processLength(l);
    processBlock();
  }
  
  public void reset() {
    this.byteCount = 0L;
    this.xBufOff = 0;
    for (byte b = 0; b < this.xBuf.length; b++)
      this.xBuf[b] = 0; 
  }
  
  protected void populateState(byte[] paramArrayOfbyte) {
    System.arraycopy(this.xBuf, 0, paramArrayOfbyte, 0, this.xBufOff);
    Pack.intToBigEndian(this.xBufOff, paramArrayOfbyte, 4);
    Pack.longToBigEndian(this.byteCount, paramArrayOfbyte, 8);
  }
  
  public int getByteLength() {
    return 64;
  }
  
  protected abstract void processWord(byte[] paramArrayOfbyte, int paramInt);
  
  protected abstract void processLength(long paramLong);
  
  protected abstract void processBlock();
}
