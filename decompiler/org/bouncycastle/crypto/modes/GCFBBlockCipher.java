package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;

public class GCFBBlockCipher extends StreamBlockCipher {
  private static final byte[] C = new byte[] { 
      105, 0, 114, 34, 100, -55, 4, 35, -115, 58, 
      -37, -106, 70, -23, 42, -60, 24, -2, -84, -108, 
      0, -19, 7, 18, -64, -122, -36, -62, -17, 76, 
      -87, 43 };
  
  private final CFBBlockCipher cfbEngine;
  
  private KeyParameter key;
  
  private long counter = 0L;
  
  private boolean forEncryption;
  
  public GCFBBlockCipher(BlockCipher paramBlockCipher) {
    super(paramBlockCipher);
    this.cfbEngine = new CFBBlockCipher(paramBlockCipher, paramBlockCipher.getBlockSize() * 8);
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    this.counter = 0L;
    this.cfbEngine.init(paramBoolean, paramCipherParameters);
    this.forEncryption = paramBoolean;
    if (paramCipherParameters instanceof ParametersWithIV)
      paramCipherParameters = ((ParametersWithIV)paramCipherParameters).getParameters(); 
    if (paramCipherParameters instanceof ParametersWithRandom)
      paramCipherParameters = ((ParametersWithRandom)paramCipherParameters).getParameters(); 
    if (paramCipherParameters instanceof ParametersWithSBox)
      paramCipherParameters = ((ParametersWithSBox)paramCipherParameters).getParameters(); 
    this.key = (KeyParameter)paramCipherParameters;
  }
  
  public String getAlgorithmName() {
    String str = this.cfbEngine.getAlgorithmName();
    return str.substring(0, str.indexOf('/')) + "/G" + str.substring(str.indexOf('/') + 1);
  }
  
  public int getBlockSize() {
    return this.cfbEngine.getBlockSize();
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    processBytes(paramArrayOfbyte1, paramInt1, this.cfbEngine.getBlockSize(), paramArrayOfbyte2, paramInt2);
    return this.cfbEngine.getBlockSize();
  }
  
  protected byte calculateByte(byte paramByte) {
    if (this.counter > 0L && this.counter % 1024L == 0L) {
      BlockCipher blockCipher = this.cfbEngine.getUnderlyingCipher();
      blockCipher.init(false, (CipherParameters)this.key);
      byte[] arrayOfByte1 = new byte[32];
      blockCipher.processBlock(C, 0, arrayOfByte1, 0);
      blockCipher.processBlock(C, 8, arrayOfByte1, 8);
      blockCipher.processBlock(C, 16, arrayOfByte1, 16);
      blockCipher.processBlock(C, 24, arrayOfByte1, 24);
      this.key = new KeyParameter(arrayOfByte1);
      blockCipher.init(true, (CipherParameters)this.key);
      byte[] arrayOfByte2 = this.cfbEngine.getCurrentIV();
      blockCipher.processBlock(arrayOfByte2, 0, arrayOfByte2, 0);
      this.cfbEngine.init(this.forEncryption, (CipherParameters)new ParametersWithIV((CipherParameters)this.key, arrayOfByte2));
    } 
    this.counter++;
    return this.cfbEngine.calculateByte(paramByte);
  }
  
  public void reset() {
    this.counter = 0L;
    this.cfbEngine.reset();
  }
}
