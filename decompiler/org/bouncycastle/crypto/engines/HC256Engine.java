package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class HC256Engine implements StreamCipher {
  private int[] p = new int[1024];
  
  private int[] q = new int[1024];
  
  private int cnt = 0;
  
  private byte[] key;
  
  private byte[] iv;
  
  private boolean initialised;
  
  private byte[] buf = new byte[4];
  
  private int idx = 0;
  
  private int step() {
    int j;
    int i = this.cnt & 0x3FF;
    if (this.cnt < 1024) {
      int k = this.p[i - 3 & 0x3FF];
      int m = this.p[i - 1023 & 0x3FF];
      this.p[i] = this.p[i] + this.p[i - 10 & 0x3FF] + (rotateRight(k, 10) ^ rotateRight(m, 23)) + this.q[(k ^ m) & 0x3FF];
      k = this.p[i - 12 & 0x3FF];
      j = this.q[k & 0xFF] + this.q[(k >> 8 & 0xFF) + 256] + this.q[(k >> 16 & 0xFF) + 512] + this.q[(k >> 24 & 0xFF) + 768] ^ this.p[i];
    } else {
      int k = this.q[i - 3 & 0x3FF];
      int m = this.q[i - 1023 & 0x3FF];
      this.q[i] = this.q[i] + this.q[i - 10 & 0x3FF] + (rotateRight(k, 10) ^ rotateRight(m, 23)) + this.p[(k ^ m) & 0x3FF];
      k = this.q[i - 12 & 0x3FF];
      j = this.p[k & 0xFF] + this.p[(k >> 8 & 0xFF) + 256] + this.p[(k >> 16 & 0xFF) + 512] + this.p[(k >> 24 & 0xFF) + 768] ^ this.q[i];
    } 
    this.cnt = this.cnt + 1 & 0x7FF;
    return j;
  }
  
  private void init() {
    if (this.key.length != 32 && this.key.length != 16)
      throw new IllegalArgumentException("The key must be 128/256 bits long"); 
    if (this.iv.length < 16)
      throw new IllegalArgumentException("The IV must be at least 128 bits long"); 
    if (this.key.length != 32) {
      byte[] arrayOfByte = new byte[32];
      System.arraycopy(this.key, 0, arrayOfByte, 0, this.key.length);
      System.arraycopy(this.key, 0, arrayOfByte, 16, this.key.length);
      this.key = arrayOfByte;
    } 
    if (this.iv.length < 32) {
      byte[] arrayOfByte = new byte[32];
      System.arraycopy(this.iv, 0, arrayOfByte, 0, this.iv.length);
      System.arraycopy(this.iv, 0, arrayOfByte, this.iv.length, arrayOfByte.length - this.iv.length);
      this.iv = arrayOfByte;
    } 
    this.idx = 0;
    this.cnt = 0;
    int[] arrayOfInt = new int[2560];
    byte b;
    for (b = 0; b < 32; b++)
      arrayOfInt[b >> 2] = arrayOfInt[b >> 2] | (this.key[b] & 0xFF) << 8 * (b & 0x3); 
    for (b = 0; b < 32; b++)
      arrayOfInt[(b >> 2) + 8] = arrayOfInt[(b >> 2) + 8] | (this.iv[b] & 0xFF) << 8 * (b & 0x3); 
    for (b = 16; b < '਀'; b++) {
      int i = arrayOfInt[b - 2];
      int j = arrayOfInt[b - 15];
      arrayOfInt[b] = (rotateRight(i, 17) ^ rotateRight(i, 19) ^ i >>> 10) + arrayOfInt[b - 7] + (rotateRight(j, 7) ^ rotateRight(j, 18) ^ j >>> 3) + arrayOfInt[b - 16] + b;
    } 
    System.arraycopy(arrayOfInt, 512, this.p, 0, 1024);
    System.arraycopy(arrayOfInt, 1536, this.q, 0, 1024);
    for (b = 0; b < 'က'; b++)
      step(); 
    this.cnt = 0;
  }
  
  public String getAlgorithmName() {
    return "HC-256";
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
      throw new IllegalArgumentException("Invalid parameter passed to HC256 init - " + paramCipherParameters.getClass().getName());
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
  
  private static int rotateRight(int paramInt1, int paramInt2) {
    return paramInt1 >>> paramInt2 | paramInt1 << -paramInt2;
  }
}
