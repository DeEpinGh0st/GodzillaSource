package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;

public class NullEngine implements BlockCipher {
  private boolean initialised;
  
  protected static final int DEFAULT_BLOCK_SIZE = 1;
  
  private final int blockSize;
  
  public NullEngine() {
    this(1);
  }
  
  public NullEngine(int paramInt) {
    this.blockSize = paramInt;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    this.initialised = true;
  }
  
  public String getAlgorithmName() {
    return "Null";
  }
  
  public int getBlockSize() {
    return this.blockSize;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (!this.initialised)
      throw new IllegalStateException("Null engine not initialised"); 
    if (paramInt1 + this.blockSize > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + this.blockSize > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    for (byte b = 0; b < this.blockSize; b++)
      paramArrayOfbyte2[paramInt2 + b] = paramArrayOfbyte1[paramInt1 + b]; 
    return this.blockSize;
  }
  
  public void reset() {}
}
