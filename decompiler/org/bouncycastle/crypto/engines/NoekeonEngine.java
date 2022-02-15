package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public class NoekeonEngine implements BlockCipher {
  private static final int genericSize = 16;
  
  private static final int[] nullVector = new int[] { 0, 0, 0, 0 };
  
  private static final int[] roundConstants = new int[] { 
      128, 27, 54, 108, 216, 171, 77, 154, 47, 94, 
      188, 99, 198, 151, 53, 106, 212 };
  
  private int[] state = new int[4];
  
  private int[] subKeys = new int[4];
  
  private int[] decryptKeys = new int[4];
  
  private boolean _initialised = false;
  
  private boolean _forEncryption;
  
  public String getAlgorithmName() {
    return "Noekeon";
  }
  
  public int getBlockSize() {
    return 16;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (!(paramCipherParameters instanceof KeyParameter))
      throw new IllegalArgumentException("invalid parameter passed to Noekeon init - " + paramCipherParameters.getClass().getName()); 
    this._forEncryption = paramBoolean;
    this._initialised = true;
    KeyParameter keyParameter = (KeyParameter)paramCipherParameters;
    setKey(keyParameter.getKey());
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    if (!this._initialised)
      throw new IllegalStateException(getAlgorithmName() + " not initialised"); 
    if (paramInt1 + 16 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + 16 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    return this._forEncryption ? encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2) : decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
  }
  
  public void reset() {}
  
  private void setKey(byte[] paramArrayOfbyte) {
    this.subKeys[0] = bytesToIntBig(paramArrayOfbyte, 0);
    this.subKeys[1] = bytesToIntBig(paramArrayOfbyte, 4);
    this.subKeys[2] = bytesToIntBig(paramArrayOfbyte, 8);
    this.subKeys[3] = bytesToIntBig(paramArrayOfbyte, 12);
  }
  
  private int encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    this.state[0] = bytesToIntBig(paramArrayOfbyte1, paramInt1);
    this.state[1] = bytesToIntBig(paramArrayOfbyte1, paramInt1 + 4);
    this.state[2] = bytesToIntBig(paramArrayOfbyte1, paramInt1 + 8);
    this.state[3] = bytesToIntBig(paramArrayOfbyte1, paramInt1 + 12);
    byte b;
    for (b = 0; b < 16; b++) {
      this.state[0] = this.state[0] ^ roundConstants[b];
      theta(this.state, this.subKeys);
      pi1(this.state);
      gamma(this.state);
      pi2(this.state);
    } 
    this.state[0] = this.state[0] ^ roundConstants[b];
    theta(this.state, this.subKeys);
    intToBytesBig(this.state[0], paramArrayOfbyte2, paramInt2);
    intToBytesBig(this.state[1], paramArrayOfbyte2, paramInt2 + 4);
    intToBytesBig(this.state[2], paramArrayOfbyte2, paramInt2 + 8);
    intToBytesBig(this.state[3], paramArrayOfbyte2, paramInt2 + 12);
    return 16;
  }
  
  private int decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    this.state[0] = bytesToIntBig(paramArrayOfbyte1, paramInt1);
    this.state[1] = bytesToIntBig(paramArrayOfbyte1, paramInt1 + 4);
    this.state[2] = bytesToIntBig(paramArrayOfbyte1, paramInt1 + 8);
    this.state[3] = bytesToIntBig(paramArrayOfbyte1, paramInt1 + 12);
    System.arraycopy(this.subKeys, 0, this.decryptKeys, 0, this.subKeys.length);
    theta(this.decryptKeys, nullVector);
    byte b;
    for (b = 16; b > 0; b--) {
      theta(this.state, this.decryptKeys);
      this.state[0] = this.state[0] ^ roundConstants[b];
      pi1(this.state);
      gamma(this.state);
      pi2(this.state);
    } 
    theta(this.state, this.decryptKeys);
    this.state[0] = this.state[0] ^ roundConstants[b];
    intToBytesBig(this.state[0], paramArrayOfbyte2, paramInt2);
    intToBytesBig(this.state[1], paramArrayOfbyte2, paramInt2 + 4);
    intToBytesBig(this.state[2], paramArrayOfbyte2, paramInt2 + 8);
    intToBytesBig(this.state[3], paramArrayOfbyte2, paramInt2 + 12);
    return 16;
  }
  
  private void gamma(int[] paramArrayOfint) {
    paramArrayOfint[1] = paramArrayOfint[1] ^ (paramArrayOfint[3] ^ 0xFFFFFFFF) & (paramArrayOfint[2] ^ 0xFFFFFFFF);
    paramArrayOfint[0] = paramArrayOfint[0] ^ paramArrayOfint[2] & paramArrayOfint[1];
    int i = paramArrayOfint[3];
    paramArrayOfint[3] = paramArrayOfint[0];
    paramArrayOfint[0] = i;
    paramArrayOfint[2] = paramArrayOfint[2] ^ paramArrayOfint[0] ^ paramArrayOfint[1] ^ paramArrayOfint[3];
    paramArrayOfint[1] = paramArrayOfint[1] ^ (paramArrayOfint[3] ^ 0xFFFFFFFF) & (paramArrayOfint[2] ^ 0xFFFFFFFF);
    paramArrayOfint[0] = paramArrayOfint[0] ^ paramArrayOfint[2] & paramArrayOfint[1];
  }
  
  private void theta(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int i = paramArrayOfint1[0] ^ paramArrayOfint1[2];
    i ^= rotl(i, 8) ^ rotl(i, 24);
    paramArrayOfint1[1] = paramArrayOfint1[1] ^ i;
    paramArrayOfint1[3] = paramArrayOfint1[3] ^ i;
    for (byte b = 0; b < 4; b++)
      paramArrayOfint1[b] = paramArrayOfint1[b] ^ paramArrayOfint2[b]; 
    i = paramArrayOfint1[1] ^ paramArrayOfint1[3];
    i ^= rotl(i, 8) ^ rotl(i, 24);
    paramArrayOfint1[0] = paramArrayOfint1[0] ^ i;
    paramArrayOfint1[2] = paramArrayOfint1[2] ^ i;
  }
  
  private void pi1(int[] paramArrayOfint) {
    paramArrayOfint[1] = rotl(paramArrayOfint[1], 1);
    paramArrayOfint[2] = rotl(paramArrayOfint[2], 5);
    paramArrayOfint[3] = rotl(paramArrayOfint[3], 2);
  }
  
  private void pi2(int[] paramArrayOfint) {
    paramArrayOfint[1] = rotl(paramArrayOfint[1], 31);
    paramArrayOfint[2] = rotl(paramArrayOfint[2], 27);
    paramArrayOfint[3] = rotl(paramArrayOfint[3], 30);
  }
  
  private int bytesToIntBig(byte[] paramArrayOfbyte, int paramInt) {
    return paramArrayOfbyte[paramInt++] << 24 | (paramArrayOfbyte[paramInt++] & 0xFF) << 16 | (paramArrayOfbyte[paramInt++] & 0xFF) << 8 | paramArrayOfbyte[paramInt] & 0xFF;
  }
  
  private void intToBytesBig(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2++] = (byte)(paramInt1 >>> 24);
    paramArrayOfbyte[paramInt2++] = (byte)(paramInt1 >>> 16);
    paramArrayOfbyte[paramInt2++] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
  }
  
  private int rotl(int paramInt1, int paramInt2) {
    return paramInt1 << paramInt2 | paramInt1 >>> 32 - paramInt2;
  }
}
