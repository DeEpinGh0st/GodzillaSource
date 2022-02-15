package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;

public class BlockCipherMac implements Mac {
  private byte[] mac;
  
  private byte[] buf;
  
  private int bufOff;
  
  private BlockCipher cipher;
  
  private int macSize;
  
  public BlockCipherMac(BlockCipher paramBlockCipher) {
    this(paramBlockCipher, paramBlockCipher.getBlockSize() * 8 / 2);
  }
  
  public BlockCipherMac(BlockCipher paramBlockCipher, int paramInt) {
    if (paramInt % 8 != 0)
      throw new IllegalArgumentException("MAC size must be multiple of 8"); 
    this.cipher = (BlockCipher)new CBCBlockCipher(paramBlockCipher);
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
    while (this.bufOff < i) {
      this.buf[this.bufOff] = 0;
      this.bufOff++;
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
