package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public class TEAEngine implements BlockCipher {
  private static final int rounds = 32;
  
  private static final int block_size = 8;
  
  private static final int delta = -1640531527;
  
  private static final int d_sum = -957401312;
  
  private int _a;
  
  private int _b;
  
  private int _c;
  
  private int _d;
  
  private boolean _initialised = false;
  
  private boolean _forEncryption;
  
  public String getAlgorithmName() {
    return "TEA";
  }
  
  public int getBlockSize() {
    return 8;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (!(paramCipherParameters instanceof KeyParameter))
      throw new IllegalArgumentException("invalid parameter passed to TEA init - " + paramCipherParameters.getClass().getName()); 
    this._forEncryption = paramBoolean;
    this._initialised = true;
    KeyParameter keyParameter = (KeyParameter)paramCipherParameters;
    setKey(keyParameter.getKey());
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    if (!this._initialised)
      throw new IllegalStateException(getAlgorithmName() + " not initialised"); 
    if (paramInt1 + 8 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + 8 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    return this._forEncryption ? encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2) : decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
  }
  
  public void reset() {}
  
  private void setKey(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length != 16)
      throw new IllegalArgumentException("Key size must be 128 bits."); 
    this._a = bytesToInt(paramArrayOfbyte, 0);
    this._b = bytesToInt(paramArrayOfbyte, 4);
    this._c = bytesToInt(paramArrayOfbyte, 8);
    this._d = bytesToInt(paramArrayOfbyte, 12);
  }
  
  private int encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = bytesToInt(paramArrayOfbyte1, paramInt1);
    int j = bytesToInt(paramArrayOfbyte1, paramInt1 + 4);
    int k = 0;
    for (byte b = 0; b != 32; b++) {
      k -= 1640531527;
      i += (j << 4) + this._a ^ j + k ^ (j >>> 5) + this._b;
      j += (i << 4) + this._c ^ i + k ^ (i >>> 5) + this._d;
    } 
    unpackInt(i, paramArrayOfbyte2, paramInt2);
    unpackInt(j, paramArrayOfbyte2, paramInt2 + 4);
    return 8;
  }
  
  private int decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = bytesToInt(paramArrayOfbyte1, paramInt1);
    int j = bytesToInt(paramArrayOfbyte1, paramInt1 + 4);
    int k = -957401312;
    for (byte b = 0; b != 32; b++) {
      j -= (i << 4) + this._c ^ i + k ^ (i >>> 5) + this._d;
      i -= (j << 4) + this._a ^ j + k ^ (j >>> 5) + this._b;
      k += 1640531527;
    } 
    unpackInt(i, paramArrayOfbyte2, paramInt2);
    unpackInt(j, paramArrayOfbyte2, paramInt2 + 4);
    return 8;
  }
  
  private int bytesToInt(byte[] paramArrayOfbyte, int paramInt) {
    return paramArrayOfbyte[paramInt++] << 24 | (paramArrayOfbyte[paramInt++] & 0xFF) << 16 | (paramArrayOfbyte[paramInt++] & 0xFF) << 8 | paramArrayOfbyte[paramInt] & 0xFF;
  }
  
  private void unpackInt(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2++] = (byte)(paramInt1 >>> 24);
    paramArrayOfbyte[paramInt2++] = (byte)(paramInt1 >>> 16);
    paramArrayOfbyte[paramInt2++] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
  }
}
