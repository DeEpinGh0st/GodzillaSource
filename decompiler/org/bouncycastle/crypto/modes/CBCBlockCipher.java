package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class CBCBlockCipher implements BlockCipher {
  private byte[] IV;
  
  private byte[] cbcV;
  
  private byte[] cbcNextV;
  
  private int blockSize;
  
  private BlockCipher cipher = null;
  
  private boolean encrypting;
  
  public CBCBlockCipher(BlockCipher paramBlockCipher) {
    this.cipher = paramBlockCipher;
    this.blockSize = paramBlockCipher.getBlockSize();
    this.IV = new byte[this.blockSize];
    this.cbcV = new byte[this.blockSize];
    this.cbcNextV = new byte[this.blockSize];
  }
  
  public BlockCipher getUnderlyingCipher() {
    return this.cipher;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    boolean bool = this.encrypting;
    this.encrypting = paramBoolean;
    if (paramCipherParameters instanceof ParametersWithIV) {
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      byte[] arrayOfByte = parametersWithIV.getIV();
      if (arrayOfByte.length != this.blockSize)
        throw new IllegalArgumentException("initialisation vector must be the same length as block size"); 
      System.arraycopy(arrayOfByte, 0, this.IV, 0, arrayOfByte.length);
      reset();
      if (parametersWithIV.getParameters() != null) {
        this.cipher.init(paramBoolean, parametersWithIV.getParameters());
      } else if (bool != paramBoolean) {
        throw new IllegalArgumentException("cannot change encrypting state without providing key.");
      } 
    } else {
      reset();
      if (paramCipherParameters != null) {
        this.cipher.init(paramBoolean, paramCipherParameters);
      } else if (bool != paramBoolean) {
        throw new IllegalArgumentException("cannot change encrypting state without providing key.");
      } 
    } 
  }
  
  public String getAlgorithmName() {
    return this.cipher.getAlgorithmName() + "/CBC";
  }
  
  public int getBlockSize() {
    return this.cipher.getBlockSize();
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    return this.encrypting ? encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2) : decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
  }
  
  public void reset() {
    System.arraycopy(this.IV, 0, this.cbcV, 0, this.IV.length);
    Arrays.fill(this.cbcNextV, (byte)0);
    this.cipher.reset();
  }
  
  private int encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramInt1 + this.blockSize > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    int i;
    for (i = 0; i < this.blockSize; i++)
      this.cbcV[i] = (byte)(this.cbcV[i] ^ paramArrayOfbyte1[paramInt1 + i]); 
    i = this.cipher.processBlock(this.cbcV, 0, paramArrayOfbyte2, paramInt2);
    System.arraycopy(paramArrayOfbyte2, paramInt2, this.cbcV, 0, this.cbcV.length);
    return i;
  }
  
  private int decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramInt1 + this.blockSize > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    System.arraycopy(paramArrayOfbyte1, paramInt1, this.cbcNextV, 0, this.blockSize);
    int i = this.cipher.processBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
    for (byte b = 0; b < this.blockSize; b++)
      paramArrayOfbyte2[paramInt2 + b] = (byte)(paramArrayOfbyte2[paramInt2 + b] ^ this.cbcV[b]); 
    byte[] arrayOfByte = this.cbcV;
    this.cbcV = this.cbcNextV;
    this.cbcNextV = arrayOfByte;
    return i;
  }
}
