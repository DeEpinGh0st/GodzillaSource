package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class CBCBlockCipherMac implements Mac {
  private byte[] mac;
  
  private byte[] buf;
  
  private int bufOff;
  
  private BlockCipher cipher;
  
  private BlockCipherPadding padding;
  
  private int macSize;
  
  public CBCBlockCipherMac(BlockCipher paramBlockCipher) {
    this(paramBlockCipher, paramBlockCipher.getBlockSize() * 8 / 2, null);
  }
  
  public CBCBlockCipherMac(BlockCipher paramBlockCipher, BlockCipherPadding paramBlockCipherPadding) {
    this(paramBlockCipher, paramBlockCipher.getBlockSize() * 8 / 2, paramBlockCipherPadding);
  }
  
  public CBCBlockCipherMac(BlockCipher paramBlockCipher, int paramInt) {
    this(paramBlockCipher, paramInt, null);
  }
  
  public CBCBlockCipherMac(BlockCipher paramBlockCipher, int paramInt, BlockCipherPadding paramBlockCipherPadding) {
    if (paramInt % 8 != 0)
      throw new IllegalArgumentException("MAC size must be multiple of 8"); 
    this.cipher = (BlockCipher)new CBCBlockCipher(paramBlockCipher);
    this.padding = paramBlockCipherPadding;
    this.macSize = paramInt / 8;
    this.mac = new byte[paramBlockCipher.getBlockSize()];
    this.buf = new byte[paramBlockCipher.getBlockSize()];
    this.bufOff = 0;
  }
  
  public String getAlgorithmName() {
    return this.cipher.getAlgorithmName();
  }
  
  public void init(CipherParameters paramCipherParameters) {
    reset();
    this.cipher.init(true, paramCipherParameters);
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
    int j = i - this.bufOff;
    if (paramInt2 > j) {
      System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, j);
      this.cipher.processBlock(this.buf, 0, this.mac, 0);
      this.bufOff = 0;
      paramInt2 -= j;
      for (paramInt1 += j; paramInt2 > i; paramInt1 += i) {
        this.cipher.processBlock(paramArrayOfbyte, paramInt1, this.mac, 0);
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
      if (this.bufOff == i) {
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
        this.bufOff = 0;
      } 
      this.padding.addPadding(this.buf, this.bufOff);
    } 
    this.cipher.processBlock(this.buf, 0, this.mac, 0);
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
