package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Pack;

public class KXTSBlockCipher extends BufferedBlockCipher {
  private static final long RED_POLY_128 = 135L;
  
  private static final long RED_POLY_256 = 1061L;
  
  private static final long RED_POLY_512 = 293L;
  
  private final int blockSize;
  
  private final long reductionPolynomial;
  
  private final long[] tw_init;
  
  private final long[] tw_current;
  
  private int counter;
  
  protected static long getReductionPolynomial(int paramInt) {
    switch (paramInt) {
      case 16:
        return 135L;
      case 32:
        return 1061L;
      case 64:
        return 293L;
    } 
    throw new IllegalArgumentException("Only 128, 256, and 512 -bit block sizes supported");
  }
  
  public KXTSBlockCipher(BlockCipher paramBlockCipher) {
    this.cipher = paramBlockCipher;
    this.blockSize = paramBlockCipher.getBlockSize();
    this.reductionPolynomial = getReductionPolynomial(this.blockSize);
    this.tw_init = new long[this.blockSize >>> 3];
    this.tw_current = new long[this.blockSize >>> 3];
    this.counter = -1;
  }
  
  public int getOutputSize(int paramInt) {
    return paramInt;
  }
  
  public int getUpdateOutputSize(int paramInt) {
    return paramInt;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (!(paramCipherParameters instanceof ParametersWithIV))
      throw new IllegalArgumentException("Invalid parameters passed"); 
    ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
    paramCipherParameters = parametersWithIV.getParameters();
    byte[] arrayOfByte1 = parametersWithIV.getIV();
    if (arrayOfByte1.length != this.blockSize)
      throw new IllegalArgumentException("Currently only support IVs of exactly one block"); 
    byte[] arrayOfByte2 = new byte[this.blockSize];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, this.blockSize);
    this.cipher.init(true, paramCipherParameters);
    this.cipher.processBlock(arrayOfByte2, 0, arrayOfByte2, 0);
    this.cipher.init(paramBoolean, paramCipherParameters);
    Pack.littleEndianToLong(arrayOfByte2, 0, this.tw_init);
    System.arraycopy(this.tw_init, 0, this.tw_current, 0, this.tw_init.length);
    this.counter = 0;
  }
  
  public int processByte(byte paramByte, byte[] paramArrayOfbyte, int paramInt) {
    throw new IllegalStateException("unsupported operation");
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    if (paramArrayOfbyte1.length - paramInt1 < paramInt2)
      throw new DataLengthException("Input buffer too short"); 
    if (paramArrayOfbyte2.length - paramInt1 < paramInt2)
      throw new OutputLengthException("Output buffer too short"); 
    if (paramInt2 % this.blockSize != 0)
      throw new IllegalArgumentException("Partial blocks not supported"); 
    int i;
    for (i = 0; i < paramInt2; i += this.blockSize)
      processBlock(paramArrayOfbyte1, paramInt1 + i, paramArrayOfbyte2, paramInt3 + i); 
    return paramInt2;
  }
  
  private void processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    if (this.counter == -1)
      throw new IllegalStateException("Attempt to process too many blocks"); 
    this.counter++;
    GF_double(this.reductionPolynomial, this.tw_current);
    byte[] arrayOfByte1 = new byte[this.blockSize];
    Pack.longToLittleEndian(this.tw_current, arrayOfByte1, 0);
    byte[] arrayOfByte2 = new byte[this.blockSize];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, this.blockSize);
    byte b;
    for (b = 0; b < this.blockSize; b++)
      arrayOfByte2[b] = (byte)(arrayOfByte2[b] ^ paramArrayOfbyte1[paramInt1 + b]); 
    this.cipher.processBlock(arrayOfByte2, 0, arrayOfByte2, 0);
    for (b = 0; b < this.blockSize; b++)
      paramArrayOfbyte2[paramInt2 + b] = (byte)(arrayOfByte2[b] ^ arrayOfByte1[b]); 
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    reset();
    return 0;
  }
  
  public void reset() {
    this.cipher.reset();
    System.arraycopy(this.tw_init, 0, this.tw_current, 0, this.tw_init.length);
    this.counter = 0;
  }
  
  private static void GF_double(long paramLong, long[] paramArrayOflong) {
    long l = 0L;
    for (byte b = 0; b < paramArrayOflong.length; b++) {
      long l1 = paramArrayOflong[b];
      long l2 = l1 >>> 63L;
      paramArrayOflong[b] = l1 << 1L ^ l;
      l = l2;
    } 
    paramArrayOflong[0] = paramArrayOflong[0] ^ paramLong & -l;
  }
}
