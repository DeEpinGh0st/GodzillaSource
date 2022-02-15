package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public class DESedeEngine extends DESEngine {
  protected static final int BLOCK_SIZE = 8;
  
  private int[] workingKey1 = null;
  
  private int[] workingKey2 = null;
  
  private int[] workingKey3 = null;
  
  private boolean forEncryption;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (!(paramCipherParameters instanceof KeyParameter))
      throw new IllegalArgumentException("invalid parameter passed to DESede init - " + paramCipherParameters.getClass().getName()); 
    byte[] arrayOfByte1 = ((KeyParameter)paramCipherParameters).getKey();
    if (arrayOfByte1.length != 24 && arrayOfByte1.length != 16)
      throw new IllegalArgumentException("key size must be 16 or 24 bytes."); 
    this.forEncryption = paramBoolean;
    byte[] arrayOfByte2 = new byte[8];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte2.length);
    this.workingKey1 = generateWorkingKey(paramBoolean, arrayOfByte2);
    byte[] arrayOfByte3 = new byte[8];
    System.arraycopy(arrayOfByte1, 8, arrayOfByte3, 0, arrayOfByte3.length);
    this.workingKey2 = generateWorkingKey(!paramBoolean, arrayOfByte3);
    if (arrayOfByte1.length == 24) {
      byte[] arrayOfByte = new byte[8];
      System.arraycopy(arrayOfByte1, 16, arrayOfByte, 0, arrayOfByte.length);
      this.workingKey3 = generateWorkingKey(paramBoolean, arrayOfByte);
    } else {
      this.workingKey3 = this.workingKey1;
    } 
  }
  
  public String getAlgorithmName() {
    return "DESede";
  }
  
  public int getBlockSize() {
    return 8;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    if (this.workingKey1 == null)
      throw new IllegalStateException("DESede engine not initialised"); 
    if (paramInt1 + 8 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + 8 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    byte[] arrayOfByte = new byte[8];
    if (this.forEncryption) {
      desFunc(this.workingKey1, paramArrayOfbyte1, paramInt1, arrayOfByte, 0);
      desFunc(this.workingKey2, arrayOfByte, 0, arrayOfByte, 0);
      desFunc(this.workingKey3, arrayOfByte, 0, paramArrayOfbyte2, paramInt2);
    } else {
      desFunc(this.workingKey3, paramArrayOfbyte1, paramInt1, arrayOfByte, 0);
      desFunc(this.workingKey2, arrayOfByte, 0, arrayOfByte, 0);
      desFunc(this.workingKey1, arrayOfByte, 0, paramArrayOfbyte2, paramInt2);
    } 
    return 8;
  }
  
  public void reset() {}
}
