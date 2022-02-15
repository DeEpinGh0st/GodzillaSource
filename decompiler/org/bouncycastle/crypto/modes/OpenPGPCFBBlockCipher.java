package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;

public class OpenPGPCFBBlockCipher implements BlockCipher {
  private byte[] IV;
  
  private byte[] FR;
  
  private byte[] FRE;
  
  private BlockCipher cipher;
  
  private int count;
  
  private int blockSize;
  
  private boolean forEncryption;
  
  public OpenPGPCFBBlockCipher(BlockCipher paramBlockCipher) {
    this.cipher = paramBlockCipher;
    this.blockSize = paramBlockCipher.getBlockSize();
    this.IV = new byte[this.blockSize];
    this.FR = new byte[this.blockSize];
    this.FRE = new byte[this.blockSize];
  }
  
  public BlockCipher getUnderlyingCipher() {
    return this.cipher;
  }
  
  public String getAlgorithmName() {
    return this.cipher.getAlgorithmName() + "/OpenPGPCFB";
  }
  
  public int getBlockSize() {
    return this.cipher.getBlockSize();
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    return this.forEncryption ? encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2) : decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
  }
  
  public void reset() {
    this.count = 0;
    System.arraycopy(this.IV, 0, this.FR, 0, this.FR.length);
    this.cipher.reset();
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    this.forEncryption = paramBoolean;
    reset();
    this.cipher.init(true, paramCipherParameters);
  }
  
  private byte encryptByte(byte paramByte, int paramInt) {
    return (byte)(this.FRE[paramInt] ^ paramByte);
  }
  
  private int encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramInt1 + this.blockSize > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + this.blockSize > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    if (this.count > this.blockSize) {
      paramArrayOfbyte2[paramInt2] = encryptByte(paramArrayOfbyte1[paramInt1], this.blockSize - 2);
      this.FR[this.blockSize - 2] = encryptByte(paramArrayOfbyte1[paramInt1], this.blockSize - 2);
      paramArrayOfbyte2[paramInt2 + 1] = encryptByte(paramArrayOfbyte1[paramInt1 + 1], this.blockSize - 1);
      this.FR[this.blockSize - 1] = encryptByte(paramArrayOfbyte1[paramInt1 + 1], this.blockSize - 1);
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      for (byte b = 2; b < this.blockSize; b++) {
        paramArrayOfbyte2[paramInt2 + b] = encryptByte(paramArrayOfbyte1[paramInt1 + b], b - 2);
        this.FR[b - 2] = encryptByte(paramArrayOfbyte1[paramInt1 + b], b - 2);
      } 
    } else if (this.count == 0) {
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      for (byte b = 0; b < this.blockSize; b++) {
        paramArrayOfbyte2[paramInt2 + b] = encryptByte(paramArrayOfbyte1[paramInt1 + b], b);
        this.FR[b] = encryptByte(paramArrayOfbyte1[paramInt1 + b], b);
      } 
      this.count += this.blockSize;
    } else if (this.count == this.blockSize) {
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      paramArrayOfbyte2[paramInt2] = encryptByte(paramArrayOfbyte1[paramInt1], 0);
      paramArrayOfbyte2[paramInt2 + 1] = encryptByte(paramArrayOfbyte1[paramInt1 + 1], 1);
      System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
      System.arraycopy(paramArrayOfbyte2, paramInt2, this.FR, this.blockSize - 2, 2);
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      for (byte b = 2; b < this.blockSize; b++) {
        paramArrayOfbyte2[paramInt2 + b] = encryptByte(paramArrayOfbyte1[paramInt1 + b], b - 2);
        this.FR[b - 2] = encryptByte(paramArrayOfbyte1[paramInt1 + b], b - 2);
      } 
      this.count += this.blockSize;
    } 
    return this.blockSize;
  }
  
  private int decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramInt1 + this.blockSize > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + this.blockSize > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    if (this.count > this.blockSize) {
      byte b = paramArrayOfbyte1[paramInt1];
      this.FR[this.blockSize - 2] = b;
      paramArrayOfbyte2[paramInt2] = encryptByte(b, this.blockSize - 2);
      b = paramArrayOfbyte1[paramInt1 + 1];
      this.FR[this.blockSize - 1] = b;
      paramArrayOfbyte2[paramInt2 + 1] = encryptByte(b, this.blockSize - 1);
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      for (byte b1 = 2; b1 < this.blockSize; b1++) {
        b = paramArrayOfbyte1[paramInt1 + b1];
        this.FR[b1 - 2] = b;
        paramArrayOfbyte2[paramInt2 + b1] = encryptByte(b, b1 - 2);
      } 
    } else if (this.count == 0) {
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      for (byte b = 0; b < this.blockSize; b++) {
        this.FR[b] = paramArrayOfbyte1[paramInt1 + b];
        paramArrayOfbyte2[b] = encryptByte(paramArrayOfbyte1[paramInt1 + b], b);
      } 
      this.count += this.blockSize;
    } else if (this.count == this.blockSize) {
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      byte b1 = paramArrayOfbyte1[paramInt1];
      byte b2 = paramArrayOfbyte1[paramInt1 + 1];
      paramArrayOfbyte2[paramInt2] = encryptByte(b1, 0);
      paramArrayOfbyte2[paramInt2 + 1] = encryptByte(b2, 1);
      System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
      this.FR[this.blockSize - 2] = b1;
      this.FR[this.blockSize - 1] = b2;
      this.cipher.processBlock(this.FR, 0, this.FRE, 0);
      for (byte b = 2; b < this.blockSize; b++) {
        byte b3 = paramArrayOfbyte1[paramInt1 + b];
        this.FR[b - 2] = b3;
        paramArrayOfbyte2[paramInt2 + b] = encryptByte(b3, b - 2);
      } 
      this.count += this.blockSize;
    } 
    return this.blockSize;
  }
}
