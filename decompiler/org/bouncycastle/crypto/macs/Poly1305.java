package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Pack;

public class Poly1305 implements Mac {
  private static final int BLOCK_SIZE = 16;
  
  private final BlockCipher cipher;
  
  private final byte[] singleByte = new byte[1];
  
  private int r0;
  
  private int r1;
  
  private int r2;
  
  private int r3;
  
  private int r4;
  
  private int s1;
  
  private int s2;
  
  private int s3;
  
  private int s4;
  
  private int k0;
  
  private int k1;
  
  private int k2;
  
  private int k3;
  
  private final byte[] currentBlock = new byte[16];
  
  private int currentBlockOffset = 0;
  
  private int h0;
  
  private int h1;
  
  private int h2;
  
  private int h3;
  
  private int h4;
  
  public Poly1305() {
    this.cipher = null;
  }
  
  public Poly1305(BlockCipher paramBlockCipher) {
    if (paramBlockCipher.getBlockSize() != 16)
      throw new IllegalArgumentException("Poly1305 requires a 128 bit block cipher."); 
    this.cipher = paramBlockCipher;
  }
  
  public void init(CipherParameters paramCipherParameters) throws IllegalArgumentException {
    byte[] arrayOfByte = null;
    if (this.cipher != null) {
      if (!(paramCipherParameters instanceof ParametersWithIV))
        throw new IllegalArgumentException("Poly1305 requires an IV when used with a block cipher."); 
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      arrayOfByte = parametersWithIV.getIV();
      paramCipherParameters = parametersWithIV.getParameters();
    } 
    if (!(paramCipherParameters instanceof KeyParameter))
      throw new IllegalArgumentException("Poly1305 requires a key."); 
    KeyParameter keyParameter = (KeyParameter)paramCipherParameters;
    setKey(keyParameter.getKey(), arrayOfByte);
    reset();
  }
  
  private void setKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte[] arrayOfByte;
    byte b;
    if (paramArrayOfbyte1.length != 32)
      throw new IllegalArgumentException("Poly1305 key must be 256 bits."); 
    if (this.cipher != null && (paramArrayOfbyte2 == null || paramArrayOfbyte2.length != 16))
      throw new IllegalArgumentException("Poly1305 requires a 128 bit IV."); 
    int i = Pack.littleEndianToInt(paramArrayOfbyte1, 0);
    int j = Pack.littleEndianToInt(paramArrayOfbyte1, 4);
    int k = Pack.littleEndianToInt(paramArrayOfbyte1, 8);
    int m = Pack.littleEndianToInt(paramArrayOfbyte1, 12);
    this.r0 = i & 0x3FFFFFF;
    this.r1 = (i >>> 26 | j << 6) & 0x3FFFF03;
    this.r2 = (j >>> 20 | k << 12) & 0x3FFC0FF;
    this.r3 = (k >>> 14 | m << 18) & 0x3F03FFF;
    this.r4 = m >>> 8 & 0xFFFFF;
    this.s1 = this.r1 * 5;
    this.s2 = this.r2 * 5;
    this.s3 = this.r3 * 5;
    this.s4 = this.r4 * 5;
    if (this.cipher == null) {
      arrayOfByte = paramArrayOfbyte1;
      b = 16;
    } else {
      arrayOfByte = new byte[16];
      b = 0;
      this.cipher.init(true, (CipherParameters)new KeyParameter(paramArrayOfbyte1, 16, 16));
      this.cipher.processBlock(paramArrayOfbyte2, 0, arrayOfByte, 0);
    } 
    this.k0 = Pack.littleEndianToInt(arrayOfByte, b + 0);
    this.k1 = Pack.littleEndianToInt(arrayOfByte, b + 4);
    this.k2 = Pack.littleEndianToInt(arrayOfByte, b + 8);
    this.k3 = Pack.littleEndianToInt(arrayOfByte, b + 12);
  }
  
  public String getAlgorithmName() {
    return (this.cipher == null) ? "Poly1305" : ("Poly1305-" + this.cipher.getAlgorithmName());
  }
  
  public int getMacSize() {
    return 16;
  }
  
  public void update(byte paramByte) throws IllegalStateException {
    this.singleByte[0] = paramByte;
    update(this.singleByte, 0, 1);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalStateException {
    int i = 0;
    while (paramInt2 > i) {
      if (this.currentBlockOffset == 16) {
        processBlock();
        this.currentBlockOffset = 0;
      } 
      int j = Math.min(paramInt2 - i, 16 - this.currentBlockOffset);
      System.arraycopy(paramArrayOfbyte, i + paramInt1, this.currentBlock, this.currentBlockOffset, j);
      i += j;
      this.currentBlockOffset += j;
    } 
  }
  
  private void processBlock() {
    if (this.currentBlockOffset < 16) {
      this.currentBlock[this.currentBlockOffset] = 1;
      for (int i = this.currentBlockOffset + 1; i < 16; i++)
        this.currentBlock[i] = 0; 
    } 
    long l1 = 0xFFFFFFFFL & Pack.littleEndianToInt(this.currentBlock, 0);
    long l2 = 0xFFFFFFFFL & Pack.littleEndianToInt(this.currentBlock, 4);
    long l3 = 0xFFFFFFFFL & Pack.littleEndianToInt(this.currentBlock, 8);
    long l4 = 0xFFFFFFFFL & Pack.littleEndianToInt(this.currentBlock, 12);
    this.h0 = (int)(this.h0 + (l1 & 0x3FFFFFFL));
    this.h1 = (int)(this.h1 + ((l2 << 32L | l1) >>> 26L & 0x3FFFFFFL));
    this.h2 = (int)(this.h2 + ((l3 << 32L | l2) >>> 20L & 0x3FFFFFFL));
    this.h3 = (int)(this.h3 + ((l4 << 32L | l3) >>> 14L & 0x3FFFFFFL));
    this.h4 = (int)(this.h4 + (l4 >>> 8L));
    if (this.currentBlockOffset == 16)
      this.h4 += 16777216; 
    long l5 = mul32x32_64(this.h0, this.r0) + mul32x32_64(this.h1, this.s4) + mul32x32_64(this.h2, this.s3) + mul32x32_64(this.h3, this.s2) + mul32x32_64(this.h4, this.s1);
    long l6 = mul32x32_64(this.h0, this.r1) + mul32x32_64(this.h1, this.r0) + mul32x32_64(this.h2, this.s4) + mul32x32_64(this.h3, this.s3) + mul32x32_64(this.h4, this.s2);
    long l7 = mul32x32_64(this.h0, this.r2) + mul32x32_64(this.h1, this.r1) + mul32x32_64(this.h2, this.r0) + mul32x32_64(this.h3, this.s4) + mul32x32_64(this.h4, this.s3);
    long l8 = mul32x32_64(this.h0, this.r3) + mul32x32_64(this.h1, this.r2) + mul32x32_64(this.h2, this.r1) + mul32x32_64(this.h3, this.r0) + mul32x32_64(this.h4, this.s4);
    long l9 = mul32x32_64(this.h0, this.r4) + mul32x32_64(this.h1, this.r3) + mul32x32_64(this.h2, this.r2) + mul32x32_64(this.h3, this.r1) + mul32x32_64(this.h4, this.r0);
    this.h0 = (int)l5 & 0x3FFFFFF;
    l6 += l5 >>> 26L;
    this.h1 = (int)l6 & 0x3FFFFFF;
    l7 += l6 >>> 26L;
    this.h2 = (int)l7 & 0x3FFFFFF;
    l8 += l7 >>> 26L;
    this.h3 = (int)l8 & 0x3FFFFFF;
    l9 += l8 >>> 26L;
    this.h4 = (int)l9 & 0x3FFFFFF;
    this.h0 += (int)(l9 >>> 26L) * 5;
    this.h1 += this.h0 >>> 26;
    this.h0 &= 0x3FFFFFF;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException {
    if (paramInt + 16 > paramArrayOfbyte.length)
      throw new OutputLengthException("Output buffer is too short."); 
    if (this.currentBlockOffset > 0)
      processBlock(); 
    this.h1 += this.h0 >>> 26;
    this.h0 &= 0x3FFFFFF;
    this.h2 += this.h1 >>> 26;
    this.h1 &= 0x3FFFFFF;
    this.h3 += this.h2 >>> 26;
    this.h2 &= 0x3FFFFFF;
    this.h4 += this.h3 >>> 26;
    this.h3 &= 0x3FFFFFF;
    this.h0 += (this.h4 >>> 26) * 5;
    this.h4 &= 0x3FFFFFF;
    this.h1 += this.h0 >>> 26;
    this.h0 &= 0x3FFFFFF;
    int i = this.h0 + 5;
    int i1 = i >>> 26;
    i &= 0x3FFFFFF;
    int j = this.h1 + i1;
    i1 = j >>> 26;
    j &= 0x3FFFFFF;
    int k = this.h2 + i1;
    i1 = k >>> 26;
    k &= 0x3FFFFFF;
    int m = this.h3 + i1;
    i1 = m >>> 26;
    m &= 0x3FFFFFF;
    int n = this.h4 + i1 - 67108864;
    i1 = (n >>> 31) - 1;
    int i2 = i1 ^ 0xFFFFFFFF;
    this.h0 = this.h0 & i2 | i & i1;
    this.h1 = this.h1 & i2 | j & i1;
    this.h2 = this.h2 & i2 | k & i1;
    this.h3 = this.h3 & i2 | m & i1;
    this.h4 = this.h4 & i2 | n & i1;
    long l1 = ((this.h0 | this.h1 << 26) & 0xFFFFFFFFL) + (0xFFFFFFFFL & this.k0);
    long l2 = ((this.h1 >>> 6 | this.h2 << 20) & 0xFFFFFFFFL) + (0xFFFFFFFFL & this.k1);
    long l3 = ((this.h2 >>> 12 | this.h3 << 14) & 0xFFFFFFFFL) + (0xFFFFFFFFL & this.k2);
    long l4 = ((this.h3 >>> 18 | this.h4 << 8) & 0xFFFFFFFFL) + (0xFFFFFFFFL & this.k3);
    Pack.intToLittleEndian((int)l1, paramArrayOfbyte, paramInt);
    l2 += l1 >>> 32L;
    Pack.intToLittleEndian((int)l2, paramArrayOfbyte, paramInt + 4);
    l3 += l2 >>> 32L;
    Pack.intToLittleEndian((int)l3, paramArrayOfbyte, paramInt + 8);
    l4 += l3 >>> 32L;
    Pack.intToLittleEndian((int)l4, paramArrayOfbyte, paramInt + 12);
    reset();
    return 16;
  }
  
  public void reset() {
    this.currentBlockOffset = 0;
    this.h0 = this.h1 = this.h2 = this.h3 = this.h4 = 0;
  }
  
  private static final long mul32x32_64(int paramInt1, int paramInt2) {
    return (paramInt1 & 0xFFFFFFFFL) * paramInt2;
  }
}
