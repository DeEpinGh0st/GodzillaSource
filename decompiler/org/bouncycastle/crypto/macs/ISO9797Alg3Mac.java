package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class ISO9797Alg3Mac implements Mac {
  private byte[] mac;
  
  private byte[] buf;
  
  private int bufOff;
  
  private BlockCipher cipher;
  
  private BlockCipherPadding padding;
  
  private int macSize;
  
  private KeyParameter lastKey2;
  
  private KeyParameter lastKey3;
  
  public ISO9797Alg3Mac(BlockCipher paramBlockCipher) {
    this(paramBlockCipher, paramBlockCipher.getBlockSize() * 8, null);
  }
  
  public ISO9797Alg3Mac(BlockCipher paramBlockCipher, BlockCipherPadding paramBlockCipherPadding) {
    this(paramBlockCipher, paramBlockCipher.getBlockSize() * 8, paramBlockCipherPadding);
  }
  
  public ISO9797Alg3Mac(BlockCipher paramBlockCipher, int paramInt) {
    this(paramBlockCipher, paramInt, null);
  }
  
  public ISO9797Alg3Mac(BlockCipher paramBlockCipher, int paramInt, BlockCipherPadding paramBlockCipherPadding) {
    if (paramInt % 8 != 0)
      throw new IllegalArgumentException("MAC size must be multiple of 8"); 
    if (!(paramBlockCipher instanceof DESEngine))
      throw new IllegalArgumentException("cipher must be instance of DESEngine"); 
    this.cipher = (BlockCipher)new CBCBlockCipher(paramBlockCipher);
    this.padding = paramBlockCipherPadding;
    this.macSize = paramInt / 8;
    this.mac = new byte[paramBlockCipher.getBlockSize()];
    this.buf = new byte[paramBlockCipher.getBlockSize()];
    this.bufOff = 0;
  }
  
  public String getAlgorithmName() {
    return "ISO9797Alg3";
  }
  
  public void init(CipherParameters paramCipherParameters) {
    KeyParameter keyParameter1;
    KeyParameter keyParameter2;
    reset();
    if (!(paramCipherParameters instanceof KeyParameter) && !(paramCipherParameters instanceof ParametersWithIV))
      throw new IllegalArgumentException("params must be an instance of KeyParameter or ParametersWithIV"); 
    if (paramCipherParameters instanceof KeyParameter) {
      keyParameter1 = (KeyParameter)paramCipherParameters;
    } else {
      keyParameter1 = (KeyParameter)((ParametersWithIV)paramCipherParameters).getParameters();
    } 
    byte[] arrayOfByte = keyParameter1.getKey();
    if (arrayOfByte.length == 16) {
      keyParameter2 = new KeyParameter(arrayOfByte, 0, 8);
      this.lastKey2 = new KeyParameter(arrayOfByte, 8, 8);
      this.lastKey3 = keyParameter2;
    } else if (arrayOfByte.length == 24) {
      keyParameter2 = new KeyParameter(arrayOfByte, 0, 8);
      this.lastKey2 = new KeyParameter(arrayOfByte, 8, 8);
      this.lastKey3 = new KeyParameter(arrayOfByte, 16, 8);
    } else {
      throw new IllegalArgumentException("Key must be either 112 or 168 bit long");
    } 
    if (paramCipherParameters instanceof ParametersWithIV) {
      this.cipher.init(true, (CipherParameters)new ParametersWithIV((CipherParameters)keyParameter2, ((ParametersWithIV)paramCipherParameters).getIV()));
    } else {
      this.cipher.init(true, (CipherParameters)keyParameter2);
    } 
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
      if (this.bufOff == i) {
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
        this.bufOff = 0;
      } 
      this.padding.addPadding(this.buf, this.bufOff);
    } 
    this.cipher.processBlock(this.buf, 0, this.mac, 0);
    DESEngine dESEngine = new DESEngine();
    dESEngine.init(false, (CipherParameters)this.lastKey2);
    dESEngine.processBlock(this.mac, 0, this.mac, 0);
    dESEngine.init(true, (CipherParameters)this.lastKey3);
    dESEngine.processBlock(this.mac, 0, this.mac, 0);
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
