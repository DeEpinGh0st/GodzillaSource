package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;

public class NISTCTSBlockCipher extends BufferedBlockCipher {
  public static final int CS1 = 1;
  
  public static final int CS2 = 2;
  
  public static final int CS3 = 3;
  
  private final int type;
  
  private final int blockSize;
  
  public NISTCTSBlockCipher(int paramInt, BlockCipher paramBlockCipher) {
    this.type = paramInt;
    this.cipher = new CBCBlockCipher(paramBlockCipher);
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
      if (this.bufOff < i)
        throw new DataLengthException("need at least one block of input for NISTCTS"); 
      if (this.bufOff > i) {
        byte[] arrayOfByte1 = new byte[i];
        if (this.type == 2 || this.type == 3) {
          this.cipher.processBlock(this.buf, 0, arrayOfByte, 0);
          System.arraycopy(this.buf, i, arrayOfByte1, 0, j);
          this.cipher.processBlock(arrayOfByte1, 0, arrayOfByte1, 0);
          if (this.type == 2 && j == i) {
            System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt, i);
            System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt + i, j);
          } else {
            System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt, i);
            System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt + i, j);
          } 
        } else {
          System.arraycopy(this.buf, 0, arrayOfByte, 0, i);
          this.cipher.processBlock(arrayOfByte, 0, arrayOfByte, 0);
          System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt, j);
          System.arraycopy(this.buf, this.bufOff - j, arrayOfByte1, 0, j);
          this.cipher.processBlock(arrayOfByte1, 0, arrayOfByte1, 0);
          System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt + j, i);
        } 
      } else {
        this.cipher.processBlock(this.buf, 0, arrayOfByte, 0);
        System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt, i);
      } 
    } else {
      if (this.bufOff < i)
        throw new DataLengthException("need at least one block of input for CTS"); 
      byte[] arrayOfByte1 = new byte[i];
      if (this.bufOff > i) {
        if (this.type == 3 || (this.type == 2 && (this.buf.length - this.bufOff) % i != 0)) {
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
        } else {
          BlockCipher blockCipher = ((CBCBlockCipher)this.cipher).getUnderlyingCipher();
          blockCipher.processBlock(this.buf, this.bufOff - i, arrayOfByte1, 0);
          System.arraycopy(this.buf, 0, arrayOfByte, 0, i);
          if (j != i)
            System.arraycopy(arrayOfByte1, j, arrayOfByte, j, i - j); 
          this.cipher.processBlock(arrayOfByte, 0, arrayOfByte, 0);
          System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt, i);
          for (int m = 0; m != j; m++)
            arrayOfByte1[m] = (byte)(arrayOfByte1[m] ^ this.buf[m]); 
          System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt + i, j);
        } 
      } else {
        this.cipher.processBlock(this.buf, 0, arrayOfByte, 0);
        System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt, i);
      } 
    } 
    int k = this.bufOff;
    reset();
    return k;
  }
}
