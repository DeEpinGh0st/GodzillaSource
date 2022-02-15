package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class KCTRBlockCipher extends StreamBlockCipher {
  private byte[] iv;
  
  private byte[] ofbV;
  
  private byte[] ofbOutV;
  
  private int byteCount;
  
  private boolean initialised;
  
  private BlockCipher engine;
  
  public KCTRBlockCipher(BlockCipher paramBlockCipher) {
    super(paramBlockCipher);
    this.engine = paramBlockCipher;
    this.iv = new byte[paramBlockCipher.getBlockSize()];
    this.ofbV = new byte[paramBlockCipher.getBlockSize()];
    this.ofbOutV = new byte[paramBlockCipher.getBlockSize()];
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    this.initialised = true;
    if (paramCipherParameters instanceof ParametersWithIV) {
      ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
      byte[] arrayOfByte = parametersWithIV.getIV();
      int i = this.iv.length - arrayOfByte.length;
      Arrays.fill(this.iv, (byte)0);
      System.arraycopy(arrayOfByte, 0, this.iv, i, arrayOfByte.length);
      paramCipherParameters = parametersWithIV.getParameters();
    } else {
      throw new IllegalArgumentException("invalid parameter passed");
    } 
    if (paramCipherParameters != null)
      this.engine.init(true, paramCipherParameters); 
    reset();
  }
  
  public String getAlgorithmName() {
    return this.engine.getAlgorithmName() + "/KCTR";
  }
  
  public int getBlockSize() {
    return this.engine.getBlockSize();
  }
  
  protected byte calculateByte(byte paramByte) {
    if (this.byteCount == 0) {
      incrementCounterAt(0);
      checkCounter();
      this.engine.processBlock(this.ofbV, 0, this.ofbOutV, 0);
      return (byte)(this.ofbOutV[this.byteCount++] ^ paramByte);
    } 
    byte b = (byte)(this.ofbOutV[this.byteCount++] ^ paramByte);
    if (this.byteCount == this.ofbV.length)
      this.byteCount = 0; 
    return b;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramArrayOfbyte1.length - paramInt1 < getBlockSize())
      throw new DataLengthException("input buffer too short"); 
    if (paramArrayOfbyte2.length - paramInt2 < getBlockSize())
      throw new OutputLengthException("output buffer too short"); 
    processBytes(paramArrayOfbyte1, paramInt1, getBlockSize(), paramArrayOfbyte2, paramInt2);
    return getBlockSize();
  }
  
  public void reset() {
    if (this.initialised)
      this.engine.processBlock(this.iv, 0, this.ofbV, 0); 
    this.engine.reset();
    this.byteCount = 0;
  }
  
  private void incrementCounterAt(int paramInt) {
    int i = paramInt;
    do {
      this.ofbV[i++] = (byte)(this.ofbV[i++] + 1);
    } while (i < this.ofbV.length && (byte)(this.ofbV[i++] + 1) == 0);
  }
  
  private void checkCounter() {}
}
