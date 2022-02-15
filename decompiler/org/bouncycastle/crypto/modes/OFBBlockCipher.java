package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class OFBBlockCipher extends StreamBlockCipher {
  private int byteCount;
  
  private byte[] IV;
  
  private byte[] ofbV;
  
  private byte[] ofbOutV;
  
  private final int blockSize;
  
  private final BlockCipher cipher;
  
  public OFBBlockCipher(BlockCipher paramBlockCipher, int paramInt) {
    super(paramBlockCipher);
    this.cipher = paramBlockCipher;
    this.blockSize = paramInt / 8;
    this.IV = new byte[paramBlockCipher.getBlockSize()];
    this.ofbV = new byte[paramBlockCipher.getBlockSize()];
    this.ofbOutV = new byte[paramBlockCipher.getBlockSize()];
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
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
    return this.cipher.getAlgorithmName() + "/OFB" + (this.blockSize * 8);
  }
  
  public int getBlockSize() {
    return this.blockSize;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    processBytes(paramArrayOfbyte1, paramInt1, this.blockSize, paramArrayOfbyte2, paramInt2);
    return this.blockSize;
  }
  
  public void reset() {
    System.arraycopy(this.IV, 0, this.ofbV, 0, this.IV.length);
    this.byteCount = 0;
    this.cipher.reset();
  }
  
  protected byte calculateByte(byte paramByte) throws DataLengthException, IllegalStateException {
    if (this.byteCount == 0)
      this.cipher.processBlock(this.ofbV, 0, this.ofbOutV, 0); 
    byte b = (byte)(this.ofbOutV[this.byteCount++] ^ paramByte);
    if (this.byteCount == this.blockSize) {
      this.byteCount = 0;
      System.arraycopy(this.ofbV, this.blockSize, this.ofbV, 0, this.ofbV.length - this.blockSize);
      System.arraycopy(this.ofbOutV, 0, this.ofbV, this.ofbV.length - this.blockSize, this.blockSize);
    } 
    return b;
  }
}
