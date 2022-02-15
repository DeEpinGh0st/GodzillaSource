package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class RFC5649WrapEngine implements Wrapper {
  private BlockCipher engine;
  
  private KeyParameter param;
  
  private boolean forWrapping;
  
  private byte[] highOrderIV = new byte[] { -90, 89, 89, -90 };
  
  private byte[] preIV = this.highOrderIV;
  
  private byte[] extractedAIV = null;
  
  public RFC5649WrapEngine(BlockCipher paramBlockCipher) {
    this.engine = paramBlockCipher;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    this.forWrapping = paramBoolean;
    if (paramCipherParameters instanceof ParametersWithRandom)
      paramCipherParameters = ((ParametersWithRandom)paramCipherParameters).getParameters(); 
    if (paramCipherParameters instanceof KeyParameter) {
      this.param = (KeyParameter)paramCipherParameters;
      this.preIV = this.highOrderIV;
    } else if (paramCipherParameters instanceof ParametersWithIV) {
      this.preIV = ((ParametersWithIV)paramCipherParameters).getIV();
      this.param = (KeyParameter)((ParametersWithIV)paramCipherParameters).getParameters();
      if (this.preIV.length != 4)
        throw new IllegalArgumentException("IV length not equal to 4"); 
    } 
  }
  
  public String getAlgorithmName() {
    return this.engine.getAlgorithmName();
  }
  
  private byte[] padPlaintext(byte[] paramArrayOfbyte) {
    int i = paramArrayOfbyte.length;
    int j = (8 - i % 8) % 8;
    byte[] arrayOfByte = new byte[i + j];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, i);
    if (j != 0) {
      byte[] arrayOfByte1 = new byte[j];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte, i, j);
    } 
    return arrayOfByte;
  }
  
  public byte[] wrap(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (!this.forWrapping)
      throw new IllegalStateException("not set for wrapping"); 
    byte[] arrayOfByte1 = new byte[8];
    byte[] arrayOfByte2 = Pack.intToBigEndian(paramInt2);
    System.arraycopy(this.preIV, 0, arrayOfByte1, 0, this.preIV.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, this.preIV.length, arrayOfByte2.length);
    byte[] arrayOfByte3 = new byte[paramInt2];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte3, 0, paramInt2);
    byte[] arrayOfByte4 = padPlaintext(arrayOfByte3);
    if (arrayOfByte4.length == 8) {
      byte[] arrayOfByte = new byte[arrayOfByte4.length + arrayOfByte1.length];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte, 0, arrayOfByte1.length);
      System.arraycopy(arrayOfByte4, 0, arrayOfByte, arrayOfByte1.length, arrayOfByte4.length);
      this.engine.init(true, (CipherParameters)this.param);
      int i;
      for (i = 0; i < arrayOfByte.length; i += this.engine.getBlockSize())
        this.engine.processBlock(arrayOfByte, i, arrayOfByte, i); 
      return arrayOfByte;
    } 
    RFC3394WrapEngine rFC3394WrapEngine = new RFC3394WrapEngine(this.engine);
    ParametersWithIV parametersWithIV = new ParametersWithIV((CipherParameters)this.param, arrayOfByte1);
    rFC3394WrapEngine.init(true, (CipherParameters)parametersWithIV);
    return rFC3394WrapEngine.wrap(arrayOfByte4, 0, arrayOfByte4.length);
  }
  
  public byte[] unwrap(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException {
    byte[] arrayOfByte3;
    if (this.forWrapping)
      throw new IllegalStateException("not set for unwrapping"); 
    int i = paramInt2 / 8;
    if (i * 8 != paramInt2)
      throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes"); 
    if (i == 1)
      throw new InvalidCipherTextException("unwrap data must be at least 16 bytes"); 
    byte[] arrayOfByte1 = new byte[paramInt2];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte1, 0, paramInt2);
    byte[] arrayOfByte2 = new byte[paramInt2];
    if (i == 2) {
      this.engine.init(false, (CipherParameters)this.param);
      int i1;
      for (i1 = 0; i1 < arrayOfByte1.length; i1 += this.engine.getBlockSize())
        this.engine.processBlock(arrayOfByte1, i1, arrayOfByte2, i1); 
      this.extractedAIV = new byte[8];
      System.arraycopy(arrayOfByte2, 0, this.extractedAIV, 0, this.extractedAIV.length);
      arrayOfByte3 = new byte[arrayOfByte2.length - this.extractedAIV.length];
      System.arraycopy(arrayOfByte2, this.extractedAIV.length, arrayOfByte3, 0, arrayOfByte3.length);
    } else {
      arrayOfByte2 = rfc3394UnwrapNoIvCheck(paramArrayOfbyte, paramInt1, paramInt2);
      arrayOfByte3 = arrayOfByte2;
    } 
    byte[] arrayOfByte4 = new byte[4];
    byte[] arrayOfByte5 = new byte[4];
    System.arraycopy(this.extractedAIV, 0, arrayOfByte4, 0, arrayOfByte4.length);
    System.arraycopy(this.extractedAIV, arrayOfByte4.length, arrayOfByte5, 0, arrayOfByte5.length);
    int j = Pack.bigEndianToInt(arrayOfByte5, 0);
    boolean bool = true;
    if (!Arrays.constantTimeAreEqual(arrayOfByte4, this.preIV))
      bool = false; 
    int k = arrayOfByte3.length;
    int m = k - 8;
    if (j <= m)
      bool = false; 
    if (j > k)
      bool = false; 
    int n = k - j;
    if (n >= arrayOfByte3.length) {
      bool = false;
      n = arrayOfByte3.length;
    } 
    byte[] arrayOfByte6 = new byte[n];
    byte[] arrayOfByte7 = new byte[n];
    System.arraycopy(arrayOfByte3, arrayOfByte3.length - n, arrayOfByte7, 0, n);
    if (!Arrays.constantTimeAreEqual(arrayOfByte7, arrayOfByte6))
      bool = false; 
    if (!bool)
      throw new InvalidCipherTextException("checksum failed"); 
    byte[] arrayOfByte8 = new byte[j];
    System.arraycopy(arrayOfByte3, 0, arrayOfByte8, 0, arrayOfByte8.length);
    return arrayOfByte8;
  }
  
  private byte[] rfc3394UnwrapNoIvCheck(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    byte[] arrayOfByte1 = new byte[8];
    byte[] arrayOfByte2 = new byte[paramInt2 - arrayOfByte1.length];
    byte[] arrayOfByte3 = new byte[arrayOfByte1.length];
    byte[] arrayOfByte4 = new byte[8 + arrayOfByte1.length];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte3, 0, arrayOfByte1.length);
    System.arraycopy(paramArrayOfbyte, paramInt1 + arrayOfByte1.length, arrayOfByte2, 0, paramInt2 - arrayOfByte1.length);
    this.engine.init(false, (CipherParameters)this.param);
    int i = paramInt2 / 8;
    i--;
    for (byte b = 5; b >= 0; b--) {
      for (int j = i; j >= 1; j--) {
        System.arraycopy(arrayOfByte3, 0, arrayOfByte4, 0, arrayOfByte1.length);
        System.arraycopy(arrayOfByte2, 8 * (j - 1), arrayOfByte4, arrayOfByte1.length, 8);
        int k = i * b + j;
        for (byte b1 = 1; k != 0; b1++) {
          byte b2 = (byte)k;
          arrayOfByte4[arrayOfByte1.length - b1] = (byte)(arrayOfByte4[arrayOfByte1.length - b1] ^ b2);
          k >>>= 8;
        } 
        this.engine.processBlock(arrayOfByte4, 0, arrayOfByte4, 0);
        System.arraycopy(arrayOfByte4, 0, arrayOfByte3, 0, 8);
        System.arraycopy(arrayOfByte4, 8, arrayOfByte2, 8 * (j - 1), 8);
      } 
    } 
    this.extractedAIV = arrayOfByte3;
    return arrayOfByte2;
  }
}
