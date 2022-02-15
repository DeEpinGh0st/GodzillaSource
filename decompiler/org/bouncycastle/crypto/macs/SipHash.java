package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class SipHash implements Mac {
  protected final int c = 2;
  
  protected final int d = 4;
  
  protected long k0;
  
  protected long k1;
  
  protected long v0;
  
  protected long v1;
  
  protected long v2;
  
  protected long v3;
  
  protected long m = 0L;
  
  protected int wordPos = 0;
  
  protected int wordCount = 0;
  
  public SipHash() {}
  
  public SipHash(int paramInt1, int paramInt2) {}
  
  public String getAlgorithmName() {
    return "SipHash-" + this.c + "-" + this.d;
  }
  
  public int getMacSize() {
    return 8;
  }
  
  public void init(CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (!(paramCipherParameters instanceof KeyParameter))
      throw new IllegalArgumentException("'params' must be an instance of KeyParameter"); 
    KeyParameter keyParameter = (KeyParameter)paramCipherParameters;
    byte[] arrayOfByte = keyParameter.getKey();
    if (arrayOfByte.length != 16)
      throw new IllegalArgumentException("'params' must be a 128-bit key"); 
    this.k0 = Pack.littleEndianToLong(arrayOfByte, 0);
    this.k1 = Pack.littleEndianToLong(arrayOfByte, 8);
    reset();
  }
  
  public void update(byte paramByte) throws IllegalStateException {
    this.m >>>= 8L;
    this.m |= (paramByte & 0xFFL) << 56L;
    if (++this.wordPos == 8) {
      processMessageWord();
      this.wordPos = 0;
    } 
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalStateException {
    byte b = 0;
    int i = paramInt2 & 0xFFFFFFF8;
    if (this.wordPos == 0) {
      while (b < i) {
        this.m = Pack.littleEndianToLong(paramArrayOfbyte, paramInt1 + b);
        processMessageWord();
        b += 8;
      } 
      while (b < paramInt2) {
        this.m >>>= 8L;
        this.m |= (paramArrayOfbyte[paramInt1 + b] & 0xFFL) << 56L;
        b++;
      } 
      this.wordPos = paramInt2 - i;
    } else {
      int j = this.wordPos << 3;
      while (b < i) {
        long l = Pack.littleEndianToLong(paramArrayOfbyte, paramInt1 + b);
        this.m = l << j | this.m >>> -j;
        processMessageWord();
        this.m = l;
        b += 8;
      } 
      while (b < paramInt2) {
        this.m >>>= 8L;
        this.m |= (paramArrayOfbyte[paramInt1 + b] & 0xFFL) << 56L;
        if (++this.wordPos == 8) {
          processMessageWord();
          this.wordPos = 0;
        } 
        b++;
      } 
    } 
  }
  
  public long doFinal() throws DataLengthException, IllegalStateException {
    this.m >>>= 7 - this.wordPos << 3;
    this.m >>>= 8L;
    this.m |= (((this.wordCount << 3) + this.wordPos) & 0xFFL) << 56L;
    processMessageWord();
    this.v2 ^= 0xFFL;
    applySipRounds(this.d);
    long l = this.v0 ^ this.v1 ^ this.v2 ^ this.v3;
    reset();
    return l;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException {
    long l = doFinal();
    Pack.longToLittleEndian(l, paramArrayOfbyte, paramInt);
    return 8;
  }
  
  public void reset() {
    this.v0 = this.k0 ^ 0x736F6D6570736575L;
    this.v1 = this.k1 ^ 0x646F72616E646F6DL;
    this.v2 = this.k0 ^ 0x6C7967656E657261L;
    this.v3 = this.k1 ^ 0x7465646279746573L;
    this.m = 0L;
    this.wordPos = 0;
    this.wordCount = 0;
  }
  
  protected void processMessageWord() {
    this.wordCount++;
    this.v3 ^= this.m;
    applySipRounds(this.c);
    this.v0 ^= this.m;
  }
  
  protected void applySipRounds(int paramInt) {
    long l1 = this.v0;
    long l2 = this.v1;
    long l3 = this.v2;
    long l4 = this.v3;
    for (byte b = 0; b < paramInt; b++) {
      l1 += l2;
      l3 += l4;
      l2 = rotateLeft(l2, 13);
      l4 = rotateLeft(l4, 16);
      l2 ^= l1;
      l4 ^= l3;
      l1 = rotateLeft(l1, 32);
      l3 += l2;
      l1 += l4;
      l2 = rotateLeft(l2, 17);
      l4 = rotateLeft(l4, 21);
      l2 ^= l3;
      l4 ^= l1;
      l3 = rotateLeft(l3, 32);
    } 
    this.v0 = l1;
    this.v1 = l2;
    this.v2 = l3;
    this.v3 = l4;
  }
  
  protected static long rotateLeft(long paramLong, int paramInt) {
    return paramLong << paramInt | paramLong >>> -paramInt;
  }
}
