package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;

public class OldCTSBlockCipher extends BufferedBlockCipher {
  private int blockSize;
  
  public OldCTSBlockCipher(BlockCipher paramBlockCipher) {
    if (paramBlockCipher instanceof OFBBlockCipher || paramBlockCipher instanceof CFBBlockCipher)
      throw new IllegalArgumentException("CTSBlockCipher can only accept ECB, or CBC ciphers"); 
    this.cipher = paramBlockCipher;
    this.blockSize = paramBlockCipher.getBlockSize();
    this.buf = new byte[this.blockSize * 2];
    this.bufOff = 0;
  }
  
  public int getUpdateOutputSize(int paramInt) {
    int i = paramInt + this.bufOff;
    int j = i % this.buf.length;
    return (j == 0) ? (i - this.buf.length) : (i - j);
  }
  
  public int getOutputSize(int paramInt) {
    return paramInt + this.bufOff;
  }
  
  public int processByte(byte paramByte, byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException {
    int i = 0;
    if (this.bufOff == this.buf.length) {
      i = this.cipher.processBlock(this.buf, 0, paramArrayOfbyte, paramInt);
      System.arraycopy(this.buf, this.blockSize, this.buf, 0, this.blockSize);
      this.bufOff = this.blockSize;
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
      System.arraycopy(this.buf, i, this.buf, 0, i);
      this.bufOff = i;
      paramInt2 -= m;
      for (paramInt1 += m; paramInt2 > i; paramInt1 += i) {
        System.arraycopy(paramArrayOfbyte1, paramInt1, this.buf, this.bufOff, i);
        k += this.cipher.processBlock(this.buf, 0, paramArrayOfbyte2, paramInt3 + k);
        System.arraycopy(this.buf, i, this.buf, 0, i);
        paramInt2 -= i;
      } 
    } 
    System.arraycopy(paramArrayOfbyte1, paramInt1, this.buf, this.bufOff, paramInt2);
    this.bufOff += paramInt2;
    return k;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
    if (this.bufOff + paramInt > paramArrayOfbyte.length)
      throw new OutputLengthException("output buffer to small in doFinal"); 
    int i = this.cipher.getBlockSize();
    int j = this.bufOff - i;
    byte[] arrayOfByte = new byte[i];
    if (this.forEncryption) {
      this.cipher.processBlock(this.buf, 0, arrayOfByte, 0);
      if (this.bufOff < i)
        throw new DataLengthException("need at least one block of input for CTS"); 
      int m;
      for (m = this.bufOff; m != this.buf.length; m++)
        this.buf[m] = arrayOfByte[m - i]; 
      for (m = i; m != this.bufOff; m++)
        this.buf[m] = (byte)(this.buf[m] ^ arrayOfByte[m - i]); 
      if (this.cipher instanceof CBCBlockCipher) {
        BlockCipher blockCipher = ((CBCBlockCipher)this.cipher).getUnderlyingCipher();
        blockCipher.processBlock(this.buf, i, paramArrayOfbyte, paramInt);
      } else {
        this.cipher.processBlock(this.buf, i, paramArrayOfbyte, paramInt);
      } 
      System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt + i, j);
    } else {
      byte[] arrayOfByte1 = new byte[i];
      if (this.cipher instanceof CBCBlockCipher) {
        BlockCipher blockCipher = ((CBCBlockCipher)this.cipher).getUnderlyingCipher();
        blockCipher.processBlock(this.buf, 0, arrayOfByte, 0);
      } else {
        this.cipher.processBlock(this.buf, 0, arrayOfByte, 0);
      } 
      for (int m = i; m != this.bufOff; m++)
        arrayOfByte1[m - i] = (byte)(arrayOfByte[m - i] ^ this.buf[m]); 
      System.arraycopy(this.buf, i, arrayOfByte, 0, j);
      this.cipher.processBlock(arrayOfByte, 0, paramArrayOfbyte, paramInt);
      System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt + i, j);
    } 
    int k = this.bufOff;
    reset();
    return k;
  }
}
