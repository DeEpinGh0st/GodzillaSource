package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;

class MacCFBBlockCipher {
  private byte[] IV;
  
  private byte[] cfbV;
  
  private byte[] cfbOutV;
  
  private int blockSize;
  
  private BlockCipher cipher = null;
  
  public MacCFBBlockCipher(BlockCipher paramBlockCipher, int paramInt) {
    this.cipher = paramBlockCipher;
    this.blockSize = paramInt / 8;
    this.IV = new byte[paramBlockCipher.getBlockSize()];
    this.cfbV = new byte[paramBlockCipher.getBlockSize()];
    this.cfbOutV = new byte[paramBlockCipher.getBlockSize()];
  }
  
  public void init(CipherParameters paramCipherParameters) throws IllegalArgumentException {
    if (paramCipherParameters instanceof ParametersWithIV) {
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      byte[] arrayOfByte = parametersWithIV.getIV();
      if (arrayOfByte.length < this.IV.length) {
        System.arraycopy(arrayOfByte, 0, this.IV, this.IV.length - arrayOfByte.length, arrayOfByte.length);
      } else {
        System.arraycopy(arrayOfByte, 0, this.IV, 0, this.IV.length);
      } 
      reset();
      this.cipher.init(true, parametersWithIV.getParameters());
    } else {
      reset();
      this.cipher.init(true, paramCipherParameters);
    } 
  }
  
  public String getAlgorithmName() {
    return this.cipher.getAlgorithmName() + "/CFB" + (this.blockSize * 8);
  }
  
  public int getBlockSize() {
    return this.blockSize;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramInt1 + this.blockSize > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + this.blockSize > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0);
    for (byte b = 0; b < this.blockSize; b++)
      paramArrayOfbyte2[paramInt2 + b] = (byte)(this.cfbOutV[b] ^ paramArrayOfbyte1[paramInt1 + b]); 
    System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
    System.arraycopy(paramArrayOfbyte2, paramInt2, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);
    return this.blockSize;
  }
  
  public void reset() {
    System.arraycopy(this.IV, 0, this.cfbV, 0, this.IV.length);
    this.cipher.reset();
  }
  
  void getMacBlock(byte[] paramArrayOfbyte) {
    this.cipher.processBlock(this.cfbV, 0, paramArrayOfbyte, 0);
  }
}
