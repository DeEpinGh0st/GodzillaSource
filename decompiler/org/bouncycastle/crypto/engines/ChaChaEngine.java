package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Pack;

public class ChaChaEngine extends Salsa20Engine {
  public ChaChaEngine() {}
  
  public ChaChaEngine(int paramInt) {
    super(paramInt);
  }
  
  public String getAlgorithmName() {
    return "ChaCha" + this.rounds;
  }
  
  protected void advanceCounter(long paramLong) {
    int i = (int)(paramLong >>> 32L);
    int j = (int)paramLong;
    if (i > 0)
      this.engineState[13] = this.engineState[13] + i; 
    int k = this.engineState[12];
    this.engineState[12] = this.engineState[12] + j;
    if (k != 0 && this.engineState[12] < k)
      this.engineState[13] = this.engineState[13] + 1; 
  }
  
  protected void advanceCounter() {
    this.engineState[12] = this.engineState[12] + 1;
    if (this.engineState[12] + 1 == 0)
      this.engineState[13] = this.engineState[13] + 1; 
  }
  
  protected void retreatCounter(long paramLong) {
    int i = (int)(paramLong >>> 32L);
    int j = (int)paramLong;
    if (i != 0)
      if ((this.engineState[13] & 0xFFFFFFFFL) >= (i & 0xFFFFFFFFL)) {
        this.engineState[13] = this.engineState[13] - i;
      } else {
        throw new IllegalStateException("attempt to reduce counter past zero.");
      }  
    if ((this.engineState[12] & 0xFFFFFFFFL) >= (j & 0xFFFFFFFFL)) {
      this.engineState[12] = this.engineState[12] - j;
    } else if (this.engineState[13] != 0) {
      this.engineState[13] = this.engineState[13] - 1;
      this.engineState[12] = this.engineState[12] - j;
    } else {
      throw new IllegalStateException("attempt to reduce counter past zero.");
    } 
  }
  
  protected void retreatCounter() {
    if (this.engineState[12] == 0 && this.engineState[13] == 0)
      throw new IllegalStateException("attempt to reduce counter past zero."); 
    this.engineState[12] = this.engineState[12] - 1;
    if (this.engineState[12] - 1 == -1)
      this.engineState[13] = this.engineState[13] - 1; 
  }
  
  protected long getCounter() {
    return this.engineState[13] << 32L | this.engineState[12] & 0xFFFFFFFFL;
  }
  
  protected void resetCounter() {
    this.engineState[13] = 0;
    this.engineState[12] = 0;
  }
  
  protected void setKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 != null) {
      if (paramArrayOfbyte1.length != 16 && paramArrayOfbyte1.length != 32)
        throw new IllegalArgumentException(getAlgorithmName() + " requires 128 bit or 256 bit key"); 
      packTauOrSigma(paramArrayOfbyte1.length, this.engineState, 0);
      Pack.littleEndianToInt(paramArrayOfbyte1, 0, this.engineState, 4, 4);
      Pack.littleEndianToInt(paramArrayOfbyte1, paramArrayOfbyte1.length - 16, this.engineState, 8, 4);
    } 
    Pack.littleEndianToInt(paramArrayOfbyte2, 0, this.engineState, 14, 2);
  }
  
  protected void generateKeyStream(byte[] paramArrayOfbyte) {
    chachaCore(this.rounds, this.engineState, this.x);
    Pack.intToLittleEndian(this.x, paramArrayOfbyte, 0);
  }
  
  public static void chachaCore(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (paramArrayOfint1.length != 16)
      throw new IllegalArgumentException(); 
    if (paramArrayOfint2.length != 16)
      throw new IllegalArgumentException(); 
    if (paramInt % 2 != 0)
      throw new IllegalArgumentException("Number of rounds must be even"); 
    int i = paramArrayOfint1[0];
    int j = paramArrayOfint1[1];
    int k = paramArrayOfint1[2];
    int m = paramArrayOfint1[3];
    int n = paramArrayOfint1[4];
    int i1 = paramArrayOfint1[5];
    int i2 = paramArrayOfint1[6];
    int i3 = paramArrayOfint1[7];
    int i4 = paramArrayOfint1[8];
    int i5 = paramArrayOfint1[9];
    int i6 = paramArrayOfint1[10];
    int i7 = paramArrayOfint1[11];
    int i8 = paramArrayOfint1[12];
    int i9 = paramArrayOfint1[13];
    int i10 = paramArrayOfint1[14];
    int i11 = paramArrayOfint1[15];
    for (int i12 = paramInt; i12 > 0; i12 -= 2) {
      i += n;
      i8 = rotl(i8 ^ i, 16);
      i4 += i8;
      n = rotl(n ^ i4, 12);
      i += n;
      i8 = rotl(i8 ^ i, 8);
      i4 += i8;
      n = rotl(n ^ i4, 7);
      j += i1;
      i9 = rotl(i9 ^ j, 16);
      i5 += i9;
      i1 = rotl(i1 ^ i5, 12);
      j += i1;
      i9 = rotl(i9 ^ j, 8);
      i5 += i9;
      i1 = rotl(i1 ^ i5, 7);
      k += i2;
      i10 = rotl(i10 ^ k, 16);
      i6 += i10;
      i2 = rotl(i2 ^ i6, 12);
      k += i2;
      i10 = rotl(i10 ^ k, 8);
      i6 += i10;
      i2 = rotl(i2 ^ i6, 7);
      m += i3;
      i11 = rotl(i11 ^ m, 16);
      i7 += i11;
      i3 = rotl(i3 ^ i7, 12);
      m += i3;
      i11 = rotl(i11 ^ m, 8);
      i7 += i11;
      i3 = rotl(i3 ^ i7, 7);
      i += i1;
      i11 = rotl(i11 ^ i, 16);
      i6 += i11;
      i1 = rotl(i1 ^ i6, 12);
      i += i1;
      i11 = rotl(i11 ^ i, 8);
      i6 += i11;
      i1 = rotl(i1 ^ i6, 7);
      j += i2;
      i8 = rotl(i8 ^ j, 16);
      i7 += i8;
      i2 = rotl(i2 ^ i7, 12);
      j += i2;
      i8 = rotl(i8 ^ j, 8);
      i7 += i8;
      i2 = rotl(i2 ^ i7, 7);
      k += i3;
      i9 = rotl(i9 ^ k, 16);
      i4 += i9;
      i3 = rotl(i3 ^ i4, 12);
      k += i3;
      i9 = rotl(i9 ^ k, 8);
      i4 += i9;
      i3 = rotl(i3 ^ i4, 7);
      m += n;
      i10 = rotl(i10 ^ m, 16);
      i5 += i10;
      n = rotl(n ^ i5, 12);
      m += n;
      i10 = rotl(i10 ^ m, 8);
      i5 += i10;
      n = rotl(n ^ i5, 7);
    } 
    paramArrayOfint2[0] = i + paramArrayOfint1[0];
    paramArrayOfint2[1] = j + paramArrayOfint1[1];
    paramArrayOfint2[2] = k + paramArrayOfint1[2];
    paramArrayOfint2[3] = m + paramArrayOfint1[3];
    paramArrayOfint2[4] = n + paramArrayOfint1[4];
    paramArrayOfint2[5] = i1 + paramArrayOfint1[5];
    paramArrayOfint2[6] = i2 + paramArrayOfint1[6];
    paramArrayOfint2[7] = i3 + paramArrayOfint1[7];
    paramArrayOfint2[8] = i4 + paramArrayOfint1[8];
    paramArrayOfint2[9] = i5 + paramArrayOfint1[9];
    paramArrayOfint2[10] = i6 + paramArrayOfint1[10];
    paramArrayOfint2[11] = i7 + paramArrayOfint1[11];
    paramArrayOfint2[12] = i8 + paramArrayOfint1[12];
    paramArrayOfint2[13] = i9 + paramArrayOfint1[13];
    paramArrayOfint2[14] = i10 + paramArrayOfint1[14];
    paramArrayOfint2[15] = i11 + paramArrayOfint1[15];
  }
}
