package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class GOFBBlockCipher extends StreamBlockCipher {
  private byte[] IV;
  
  private byte[] ofbV;
  
  private byte[] ofbOutV;
  
  private int byteCount;
  
  private final int blockSize;
  
  private final BlockCipher cipher;
  
  boolean firstStep = true;
  
  int N3;
  
  int N4;
  
  static final int C1 = 16843012;
  
  static final int C2 = 16843009;
  
  public GOFBBlockCipher(BlockCipher paramBlockCipher) {
    super(paramBlockCipher);
    this.cipher = paramBlockCipher;
    this.blockSize = paramBlockCipher.getBlockSize();
    if (this.blockSize != 8)
      throw new IllegalArgumentException("GCTR only for 64 bit block ciphers"); 
    this.IV = new byte[paramBlockCipher.getBlockSize()];
    this.ofbV = new byte[paramBlockCipher.getBlockSize()];
    this.ofbOutV = new byte[paramBlockCipher.getBlockSize()];
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    this.firstStep = true;
    this.N3 = 0;
    this.N4 = 0;
    if (paramCipherParameters instanceof ParametersWithIV) {
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      byte[] arrayOfByte = parametersWithIV.getIV();
      if (arrayOfByte.length < this.IV.length) {
        System.arraycopy(arrayOfByte, 0, this.IV, this.IV.length - arrayOfByte.length, arrayOfByte.length);
        for (byte b = 0; b < this.IV.length - arrayOfByte.length; b++)
          this.IV[b] = 0; 
      } else {
        System.arraycopy(arrayOfByte, 0, this.IV, 0, this.IV.length);
      } 
      reset();
      if (parametersWithIV.getParameters() != null)
        this.cipher.init(true, parametersWithIV.getParameters()); 
    } else {
      reset();
      if (paramCipherParameters != null)
        this.cipher.init(true, paramCipherParameters); 
    } 
  }
  
  public String getAlgorithmName() {
    return this.cipher.getAlgorithmName() + "/GCTR";
  }
  
  public int getBlockSize() {
    return this.blockSize;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    processBytes(paramArrayOfbyte1, paramInt1, this.blockSize, paramArrayOfbyte2, paramInt2);
    return this.blockSize;
  }
  
  public void reset() {
    this.firstStep = true;
    this.N3 = 0;
    this.N4 = 0;
    System.arraycopy(this.IV, 0, this.ofbV, 0, this.IV.length);
    this.byteCount = 0;
    this.cipher.reset();
  }
  
  private int bytesToint(byte[] paramArrayOfbyte, int paramInt) {
    return (paramArrayOfbyte[paramInt + 3] << 24 & 0xFF000000) + (paramArrayOfbyte[paramInt + 2] << 16 & 0xFF0000) + (paramArrayOfbyte[paramInt + 1] << 8 & 0xFF00) + (paramArrayOfbyte[paramInt] & 0xFF);
  }
  
  private void intTobytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2 + 3] = (byte)(paramInt1 >>> 24);
    paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >>> 16);
    paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
  }
  
  protected byte calculateByte(byte paramByte) {
    if (this.byteCount == 0) {
      if (this.firstStep) {
        this.firstStep = false;
        this.cipher.processBlock(this.ofbV, 0, this.ofbOutV, 0);
        this.N3 = bytesToint(this.ofbOutV, 0);
        this.N4 = bytesToint(this.ofbOutV, 4);
      } 
      this.N3 += 16843009;
      this.N4 += 16843012;
      if (this.N4 < 16843012 && this.N4 > 0)
        this.N4++; 
      intTobytes(this.N3, this.ofbV, 0);
      intTobytes(this.N4, this.ofbV, 4);
      this.cipher.processBlock(this.ofbV, 0, this.ofbOutV, 0);
    } 
    byte b = (byte)(this.ofbOutV[this.byteCount++] ^ paramByte);
    if (this.byteCount == this.blockSize) {
      this.byteCount = 0;
      System.arraycopy(this.ofbV, this.blockSize, this.ofbV, 0, this.ofbV.length - this.blockSize);
      System.arraycopy(this.ofbOutV, 0, this.ofbV, this.ofbV.length - this.blockSize, this.blockSize);
    } 
    return b;
  }
}
