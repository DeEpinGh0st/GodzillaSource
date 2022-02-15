package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class PGPCFBBlockCipher implements BlockCipher {
  private byte[] IV;
  
  private byte[] FR;
  
  private byte[] FRE;
  
  private byte[] tmp;
  
  private BlockCipher cipher;
  
  private int count;
  
  private int blockSize;
  
  private boolean forEncryption;
  
  private boolean inlineIv;
  
  public PGPCFBBlockCipher(BlockCipher paramBlockCipher, boolean paramBoolean) {
    this.cipher = paramBlockCipher;
    this.inlineIv = paramBoolean;
    this.blockSize = paramBlockCipher.getBlockSize();
    this.IV = new byte[this.blockSize];
    this.FR = new byte[this.blockSize];
    this.FRE = new byte[this.blockSize];
    this.tmp = new byte[this.blockSize];
  }
  
  public BlockCipher getUnderlyingCipher() {
    return this.cipher;
  }
  
  public String getAlgorithmName() {
    return this.inlineIv ? (this.cipher.getAlgorithmName() + "/PGPCFBwithIV") : (this.cipher.getAlgorithmName() + "/PGPCFB");
  }
  
  public int getBlockSize() {
    return this.cipher.getBlockSize();
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    return this.inlineIv ? (this.forEncryption ? encryptBlockWithIV(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2) : decryptBlockWithIV(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2)) : (this.forEncryption ? encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2) : decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2));
  }
  
  public void reset() {
    this.count = 0;
    for (byte b = 0; b != this.FR.length; b++) {
      if (this.inlineIv) {
        this.FR[b] = 0;
      } else {
        this.FR[b] = this.IV[b];
      } 
    } 
    this.cipher.reset();
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    this.forEncryption = paramBoolean;
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
      this.cipher.init(true, parametersWithIV.getParameters());
    } else {
      reset();
      this.cipher.init(true, paramCipherParameters);
    } 
  }
  
  private byte encryptByte(byte paramByte, int paramInt) {
    return (byte)(this.FRE[paramInt] ^ paramByte);
  }
  
  private int encryptBlockWithIV(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramInt1 + this.blockSize > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (this.count == 0) {
      if (paramInt2 + 2 * this.blockSize + 2 > paramArrayOfbyte2.length)
        throw new OutputLengthException("output buffer too short"); 
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      byte b;
      for (b = 0; b < this.blockSize; b++)
        paramArrayOfbyte2[paramInt2 + b] = encryptByte(this.IV[b], b); 
      System.arraycopy(paramArrayOfbyte2, paramInt2, this.FR, 0, this.blockSize);
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      paramArrayOfbyte2[paramInt2 + this.blockSize] = encryptByte(this.IV[this.blockSize - 2], 0);
      paramArrayOfbyte2[paramInt2 + this.blockSize + 1] = encryptByte(this.IV[this.blockSize - 1], 1);
      System.arraycopy(paramArrayOfbyte2, paramInt2 + 2, this.FR, 0, this.blockSize);
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      for (b = 0; b < this.blockSize; b++)
        paramArrayOfbyte2[paramInt2 + this.blockSize + 2 + b] = encryptByte(paramArrayOfbyte1[paramInt1 + b], b); 
      System.arraycopy(paramArrayOfbyte2, paramInt2 + this.blockSize + 2, this.FR, 0, this.blockSize);
      this.count += 2 * this.blockSize + 2;
      return 2 * this.blockSize + 2;
    } 
    if (this.count >= this.blockSize + 2) {
      if (paramInt2 + this.blockSize > paramArrayOfbyte2.length)
        throw new OutputLengthException("output buffer too short"); 
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      for (byte b = 0; b < this.blockSize; b++)
        paramArrayOfbyte2[paramInt2 + b] = encryptByte(paramArrayOfbyte1[paramInt1 + b], b); 
      System.arraycopy(paramArrayOfbyte2, paramInt2, this.FR, 0, this.blockSize);
    } 
    return this.blockSize;
  }
  
  private int decryptBlockWithIV(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramInt1 + this.blockSize > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + this.blockSize > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    if (this.count == 0) {
      for (byte b = 0; b < this.blockSize; b++)
        this.FR[b] = paramArrayOfbyte1[paramInt1 + b]; 
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      this.count += this.blockSize;
      return 0;
    } 
    if (this.count == this.blockSize) {
      System.arraycopy(paramArrayOfbyte1, paramInt1, this.tmp, 0, this.blockSize);
      System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
      this.FR[this.blockSize - 2] = this.tmp[0];
      this.FR[this.blockSize - 1] = this.tmp[1];
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      for (byte b = 0; b < this.blockSize - 2; b++)
        paramArrayOfbyte2[paramInt2 + b] = encryptByte(this.tmp[b + 2], b); 
      System.arraycopy(this.tmp, 2, this.FR, 0, this.blockSize - 2);
      this.count += 2;
      return this.blockSize - 2;
    } 
    if (this.count >= this.blockSize + 2) {
      System.arraycopy(paramArrayOfbyte1, paramInt1, this.tmp, 0, this.blockSize);
      paramArrayOfbyte2[paramInt2 + 0] = encryptByte(this.tmp[0], this.blockSize - 2);
      paramArrayOfbyte2[paramInt2 + 1] = encryptByte(this.tmp[1], this.blockSize - 1);
      System.arraycopy(this.tmp, 0, this.FR, this.blockSize - 2, 2);
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      for (byte b = 0; b < this.blockSize - 2; b++)
        paramArrayOfbyte2[paramInt2 + b + 2] = encryptByte(this.tmp[b + 2], b); 
      System.arraycopy(this.tmp, 2, this.FR, 0, this.blockSize - 2);
    } 
    return this.blockSize;
  }
  
  private int encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramInt1 + this.blockSize > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + this.blockSize > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    this.cipher.processBlock(this.FR, 0, this.FRE, 0);
    byte b;
    for (b = 0; b < this.blockSize; b++)
      paramArrayOfbyte2[paramInt2 + b] = encryptByte(paramArrayOfbyte1[paramInt1 + b], b); 
    for (b = 0; b < this.blockSize; b++)
      this.FR[b] = paramArrayOfbyte2[paramInt2 + b]; 
    return this.blockSize;
  }
  
  private int decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramInt1 + this.blockSize > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + this.blockSize > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    this.cipher.processBlock(this.FR, 0, this.FRE, 0);
    byte b;
    for (b = 0; b < this.blockSize; b++)
      paramArrayOfbyte2[paramInt2 + b] = encryptByte(paramArrayOfbyte1[paramInt1 + b], b); 
    for (b = 0; b < this.blockSize; b++)
      this.FR[b] = paramArrayOfbyte1[paramInt1 + b]; 
    return this.blockSize;
  }
}
