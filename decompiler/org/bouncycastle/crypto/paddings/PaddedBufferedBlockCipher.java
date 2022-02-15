package org.bouncycastle.crypto.paddings;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public class PaddedBufferedBlockCipher extends BufferedBlockCipher {
  BlockCipherPadding padding;
  
  public PaddedBufferedBlockCipher(BlockCipher paramBlockCipher, BlockCipherPadding paramBlockCipherPadding) {
    this.cipher = paramBlockCipher;
    this.padding = paramBlockCipherPadding;
    this.buf = new byte[paramBlockCipher.getBlockSize()];
    this.bufOff = 0;
  }
  
  public PaddedBufferedBlockCipher(BlockCipher paramBlockCipher) {
    this(paramBlockCipher, new PKCS7Padding());
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    this.forEncryption = paramBoolean;
    reset();
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      this.padding.init(parametersWithRandom.getRandom());
      this.cipher.init(paramBoolean, parametersWithRandom.getParameters());
    } else {
      this.padding.init(null);
      this.cipher.init(paramBoolean, paramCipherParameters);
    } 
  }
  
  public int getOutputSize(int paramInt) {
    int i = paramInt + this.bufOff;
    int j = i % this.buf.length;
    return (j == 0) ? (this.forEncryption ? (i + this.buf.length) : i) : (i - j + this.buf.length);
  }
  
  public int getUpdateOutputSize(int paramInt) {
    int i = paramInt + this.bufOff;
    int j = i % this.buf.length;
    return (j == 0) ? Math.max(0, i - this.buf.length) : (i - j);
  }
  
  public int processByte(byte paramByte, byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException {
    int i = 0;
    if (this.bufOff == this.buf.length) {
      i = this.cipher.processBlock(this.buf, 0, paramArrayOfbyte, paramInt);
      this.bufOff = 0;
    } 
    this.buf[this.bufOff++] = paramByte;
    return i;
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws DataLengthException, IllegalStateException {
    if (paramInt2 < 0)
      throw new IllegalArgumentException("Can't have a negative input length!"); 
    int i = getBlockSize();
    int j = getUpdateOutputSize(paramInt2);
    if (j > 0 && paramInt3 + j > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    int k = 0;
    int m = this.buf.length - this.bufOff;
    if (paramInt2 > m) {
      System.arraycopy(paramArrayOfbyte1, paramInt1, this.buf, this.bufOff, m);
      k += this.cipher.processBlock(this.buf, 0, paramArrayOfbyte2, paramInt3);
      this.bufOff = 0;
      paramInt2 -= m;
      for (paramInt1 += m; paramInt2 > this.buf.length; paramInt1 += i) {
        k += this.cipher.processBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt3 + k);
        paramInt2 -= i;
      } 
    } 
    System.arraycopy(paramArrayOfbyte1, paramInt1, this.buf, this.bufOff, paramInt2);
    this.bufOff += paramInt2;
    return k;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
    int i = this.cipher.getBlockSize();
    int j = 0;
    if (this.forEncryption) {
      if (this.bufOff == i) {
        if (paramInt + 2 * i > paramArrayOfbyte.length) {
          reset();
          throw new OutputLengthException("output buffer too short");
        } 
        j = this.cipher.processBlock(this.buf, 0, paramArrayOfbyte, paramInt);
        this.bufOff = 0;
      } 
      this.padding.addPadding(this.buf, this.bufOff);
      j += this.cipher.processBlock(this.buf, 0, paramArrayOfbyte, paramInt + j);
      reset();
    } else {
      if (this.bufOff == i) {
        j = this.cipher.processBlock(this.buf, 0, this.buf, 0);
        this.bufOff = 0;
      } else {
        reset();
        throw new DataLengthException("last block incomplete in decryption");
      } 
      try {
        j -= this.padding.padCount(this.buf);
        System.arraycopy(this.buf, 0, paramArrayOfbyte, paramInt, j);
      } finally {
        reset();
      } 
    } 
    return j;
  }
}
