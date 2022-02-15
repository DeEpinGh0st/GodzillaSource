package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class HC128Engine implements StreamCipher {
  private int[] p = new int[512];
  
  private int[] q = new int[512];
  
  private int cnt = 0;
  
  private byte[] key;
  
  private byte[] iv;
  
  private boolean initialised;
  
  private byte[] buf = new byte[4];
  
  private int idx = 0;
  
  private static int f1(int paramInt) {
    return rotateRight(paramInt, 7) ^ rotateRight(paramInt, 18) ^ paramInt >>> 3;
  }
  
  private static int f2(int paramInt) {
    return rotateRight(paramInt, 17) ^ rotateRight(paramInt, 19) ^ paramInt >>> 10;
  }
  
  private int g1(int paramInt1, int paramInt2, int paramInt3) {
    return (rotateRight(paramInt1, 10) ^ rotateRight(paramInt3, 23)) + rotateRight(paramInt2, 8);
  }
  
  private int g2(int paramInt1, int paramInt2, int paramInt3) {
    return (rotateLeft(paramInt1, 10) ^ rotateLeft(paramInt3, 23)) + rotateLeft(paramInt2, 8);
  }
  
  private static int rotateLeft(int paramInt1, int paramInt2) {
    return paramInt1 << paramInt2 | paramInt1 >>> -paramInt2;
  }
  
  private static int rotateRight(int paramInt1, int paramInt2) {
    return paramInt1 >>> paramInt2 | paramInt1 << -paramInt2;
  }
  
  private int h1(int paramInt) {
    return this.q[paramInt & 0xFF] + this.q[(paramInt >> 16 & 0xFF) + 256];
  }
  
  private int h2(int paramInt) {
    return this.p[paramInt & 0xFF] + this.p[(paramInt >> 16 & 0xFF) + 256];
  }
  
  private static int mod1024(int paramInt) {
    return paramInt & 0x3FF;
  }
  
  private static int mod512(int paramInt) {
    return paramInt & 0x1FF;
  }
  
  private static int dim(int paramInt1, int paramInt2) {
    return mod512(paramInt1 - paramInt2);
  }
  
  private int step() {
    int j;
    int i = mod512(this.cnt);
    if (this.cnt < 512) {
      this.p[i] = this.p[i] + g1(this.p[dim(i, 3)], this.p[dim(i, 10)], this.p[dim(i, 511)]);
      j = h1(this.p[dim(i, 12)]) ^ this.p[i];
    } else {
      this.q[i] = this.q[i] + g2(this.q[dim(i, 3)], this.q[dim(i, 10)], this.q[dim(i, 511)]);
      j = h2(this.q[dim(i, 12)]) ^ this.q[i];
    } 
    this.cnt = mod1024(this.cnt + 1);
    return j;
  }
  
  private void init() {
    if (this.key.length != 16)
      throw new IllegalArgumentException("The key must be 128 bits long"); 
    this.idx = 0;
    this.cnt = 0;
    int[] arrayOfInt = new int[1280];
    byte b;
    for (b = 0; b < 16; b++)
      arrayOfInt[b >> 2] = arrayOfInt[b >> 2] | (this.key[b] & 0xFF) << 8 * (b & 0x3); 
    System.arraycopy(arrayOfInt, 0, arrayOfInt, 4, 4);
    for (b = 0; b < this.iv.length && b < 16; b++)
      arrayOfInt[(b >> 2) + 8] = arrayOfInt[(b >> 2) + 8] | (this.iv[b] & 0xFF) << 8 * (b & 0x3); 
    System.arraycopy(arrayOfInt, 8, arrayOfInt, 12, 4);
    for (b = 16; b < 'Ԁ'; b++)
      arrayOfInt[b] = f2(arrayOfInt[b - 2]) + arrayOfInt[b - 7] + f1(arrayOfInt[b - 15]) + arrayOfInt[b - 16] + b; 
    System.arraycopy(arrayOfInt, 256, this.p, 0, 512);
    System.arraycopy(arrayOfInt, 768, this.q, 0, 512);
    for (b = 0; b < 'Ȁ'; b++)
      this.p[b] = step(); 
    for (b = 0; b < 'Ȁ'; b++)
      this.q[b] = step(); 
    this.cnt = 0;
  }
  
  public String getAlgorithmName() {
    return "HC-128";
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    CipherParameters cipherParameters = paramCipherParameters;
    if (paramCipherParameters instanceof ParametersWithIV) {
      this.iv = ((ParametersWithIV)paramCipherParameters).getIV();
      cipherParameters = ((ParametersWithIV)paramCipherParameters).getParameters();
    } else {
      this.iv = new byte[0];
    } 
    if (cipherParameters instanceof KeyParameter) {
      this.key = ((KeyParameter)cipherParameters).getKey();
      init();
    } else {
      throw new IllegalArgumentException("Invalid parameter passed to HC128 init - " + paramCipherParameters.getClass().getName());
    } 
    this.initialised = true;
  }
  
  private byte getByte() {
    if (this.idx == 0) {
      int i = step();
      this.buf[0] = (byte)(i & 0xFF);
      i >>= 8;
      this.buf[1] = (byte)(i & 0xFF);
      i >>= 8;
      this.buf[2] = (byte)(i & 0xFF);
      i >>= 8;
      this.buf[3] = (byte)(i & 0xFF);
    } 
    byte b = this.buf[this.idx];
    this.idx = this.idx + 1 & 0x3;
    return b;
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws DataLengthException {
    if (!this.initialised)
      throw new IllegalStateException(getAlgorithmName() + " not initialised"); 
    if (paramInt1 + paramInt2 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt3 + paramInt2 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    for (byte b = 0; b < paramInt2; b++)
      paramArrayOfbyte2[paramInt3 + b] = (byte)(paramArrayOfbyte1[paramInt1 + b] ^ getByte()); 
    return paramInt2;
  }
  
  public void reset() {
    init();
  }
  
  public byte returnByte(byte paramByte) {
    return (byte)(paramByte ^ getByte());
  }
}
