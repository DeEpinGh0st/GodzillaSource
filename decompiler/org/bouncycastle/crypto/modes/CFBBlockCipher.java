package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class CFBBlockCipher extends StreamBlockCipher {
  private byte[] IV;
  
  private byte[] cfbV;
  
  private byte[] cfbOutV;
  
  private byte[] inBuf;
  
  private int blockSize;
  
  private BlockCipher cipher = null;
  
  private boolean encrypting;
  
  private int byteCount;
  
  public CFBBlockCipher(BlockCipher paramBlockCipher, int paramInt) {
    super(paramBlockCipher);
    this.cipher = paramBlockCipher;
    this.blockSize = paramInt / 8;
    this.IV = new byte[paramBlockCipher.getBlockSize()];
    this.cfbV = new byte[paramBlockCipher.getBlockSize()];
    this.cfbOutV = new byte[paramBlockCipher.getBlockSize()];
    this.inBuf = new byte[this.blockSize];
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    this.encrypting = paramBoolean;
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
    return this.cipher.getAlgorithmName() + "/CFB" + (this.blockSize * 8);
  }
  
  protected byte calculateByte(byte paramByte) throws DataLengthException, IllegalStateException {
    return this.encrypting ? encryptByte(paramByte) : decryptByte(paramByte);
  }
  
  private byte encryptByte(byte paramByte) {
    if (this.byteCount == 0)
      this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0); 
    byte b = (byte)(this.cfbOutV[this.byteCount] ^ paramByte);
    this.inBuf[this.byteCount++] = b;
    if (this.byteCount == this.blockSize) {
      this.byteCount = 0;
      System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
      System.arraycopy(this.inBuf, 0, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);
    } 
    return b;
  }
  
  private byte decryptByte(byte paramByte) {
    if (this.byteCount == 0)
      this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0); 
    this.inBuf[this.byteCount] = paramByte;
    byte b = (byte)(this.cfbOutV[this.byteCount++] ^ paramByte);
    if (this.byteCount == this.blockSize) {
      this.byteCount = 0;
      System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
      System.arraycopy(this.inBuf, 0, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);
    } 
    return b;
  }
  
  public int getBlockSize() {
    return this.blockSize;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    processBytes(paramArrayOfbyte1, paramInt1, this.blockSize, paramArrayOfbyte2, paramInt2);
    return this.blockSize;
  }
  
  public int encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    processBytes(paramArrayOfbyte1, paramInt1, this.blockSize, paramArrayOfbyte2, paramInt2);
    return this.blockSize;
  }
  
  public int decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    processBytes(paramArrayOfbyte1, paramInt1, this.blockSize, paramArrayOfbyte2, paramInt2);
    return this.blockSize;
  }
  
  public byte[] getCurrentIV() {
    return Arrays.clone(this.cfbV);
  }
  
  public void reset() {
    System.arraycopy(this.IV, 0, this.cfbV, 0, this.IV.length);
    Arrays.fill(this.inBuf, (byte)0);
    this.byteCount = 0;
    this.cipher.reset();
  }
}
