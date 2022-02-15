package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class CFBBlockCipherMac implements Mac {
  private byte[] mac;
  
  private byte[] buf;
  
  private int bufOff;
  
  private MacCFBBlockCipher cipher;
  
  private BlockCipherPadding padding = null;
  
  private int macSize;
  
  public CFBBlockCipherMac(BlockCipher paramBlockCipher) {
    this(paramBlockCipher, 8, paramBlockCipher.getBlockSize() * 8 / 2, null);
  }
  
  public CFBBlockCipherMac(BlockCipher paramBlockCipher, BlockCipherPadding paramBlockCipherPadding) {
    this(paramBlockCipher, 8, paramBlockCipher.getBlockSize() * 8 / 2, paramBlockCipherPadding);
  }
  
  public CFBBlockCipherMac(BlockCipher paramBlockCipher, int paramInt1, int paramInt2) {
    this(paramBlockCipher, paramInt1, paramInt2, null);
  }
  
  public CFBBlockCipherMac(BlockCipher paramBlockCipher, int paramInt1, int paramInt2, BlockCipherPadding paramBlockCipherPadding) {
    if (paramInt2 % 8 != 0)
      throw new IllegalArgumentException("MAC size must be multiple of 8"); 
    this.mac = new byte[paramBlockCipher.getBlockSize()];
    this.cipher = new MacCFBBlockCipher(paramBlockCipher, paramInt1);
    this.padding = paramBlockCipherPadding;
    this.macSize = paramInt2 / 8;
    this.buf = new byte[this.cipher.getBlockSize()];
    this.bufOff = 0;
  }
  
  public String getAlgorithmName() {
    return this.cipher.getAlgorithmName();
  }
  
  public void init(CipherParameters paramCipherParameters) {
    reset();
    this.cipher.init(paramCipherParameters);
  }
  
  public int getMacSize() {
    return this.macSize;
  }
  
  public void update(byte paramByte) {
    if (this.bufOff == this.buf.length) {
      this.cipher.processBlock(this.buf, 0, this.mac, 0);
      this.bufOff = 0;
    } 
    this.buf[this.bufOff++] = paramByte;
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt2 < 0)
      throw new IllegalArgumentException("Can't have a negative input length!"); 
    int i = this.cipher.getBlockSize();
    int j = 0;
    int k = i - this.bufOff;
    if (paramInt2 > k) {
      System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, k);
      j += this.cipher.processBlock(this.buf, 0, this.mac, 0);
      this.bufOff = 0;
      paramInt2 -= k;
      for (paramInt1 += k; paramInt2 > i; paramInt1 += i) {
        j += this.cipher.processBlock(paramArrayOfbyte, paramInt1, this.mac, 0);
        paramInt2 -= i;
      } 
    } 
    System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, paramInt2);
    this.bufOff += paramInt2;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    int i = this.cipher.getBlockSize();
    if (this.padding == null) {
      while (this.bufOff < i) {
        this.buf[this.bufOff] = 0;
        this.bufOff++;
      } 
    } else {
      this.padding.addPadding(this.buf, this.bufOff);
    } 
    this.cipher.processBlock(this.buf, 0, this.mac, 0);
    this.cipher.getMacBlock(this.mac);
    System.arraycopy(this.mac, 0, paramArrayOfbyte, paramInt, this.macSize);
    reset();
    return this.macSize;
  }
  
  public void reset() {
    for (byte b = 0; b < this.buf.length; b++)
      this.buf[b] = 0; 
    this.bufOff = 0;
    this.cipher.reset();
  }
}
