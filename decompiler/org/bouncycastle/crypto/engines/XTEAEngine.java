package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public class XTEAEngine implements BlockCipher {
  private static final int rounds = 32;
  
  private static final int block_size = 8;
  
  private static final int delta = -1640531527;
  
  private int[] _S = new int[4];
  
  private int[] _sum0 = new int[32];
  
  private int[] _sum1 = new int[32];
  
  private boolean _initialised = false;
  
  private boolean _forEncryption;
  
  public String getAlgorithmName() {
    return "XTEA";
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
    int i = 0;
    byte b = i;
    while (b < 4) {
      this._S[b] = bytesToInt(paramArrayOfbyte, i);
      b++;
      i += true;
    } 
    for (b = i = 0; b < 32; b++) {
      this._sum0[b] = i + this._S[i & 0x3];
      i -= 1640531527;
      this._sum1[b] = i + this._S[i >>> 11 & 0x3];
    } 
  }
  
  private int encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = bytesToInt(paramArrayOfbyte1, paramInt1);
    int j = bytesToInt(paramArrayOfbyte1, paramInt1 + 4);
    for (byte b = 0; b < 32; b++) {
      i += (j << 4 ^ j >>> 5) + j ^ this._sum0[b];
      j += (i << 4 ^ i >>> 5) + i ^ this._sum1[b];
    } 
    unpackInt(i, paramArrayOfbyte2, paramInt2);
    unpackInt(j, paramArrayOfbyte2, paramInt2 + 4);
    return 8;
  }
  
  private int decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = bytesToInt(paramArrayOfbyte1, paramInt1);
    int j = bytesToInt(paramArrayOfbyte1, paramInt1 + 4);
    for (byte b = 31; b >= 0; b--) {
      j -= (i << 4 ^ i >>> 5) + i ^ this._sum1[b];
      i -= (j << 4 ^ j >>> 5) + j ^ this._sum0[b];
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
