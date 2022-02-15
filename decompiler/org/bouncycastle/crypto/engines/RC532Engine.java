package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RC5Parameters;

public class RC532Engine implements BlockCipher {
  private int _noRounds = 12;
  
  private int[] _S = null;
  
  private static final int P32 = -1209970333;
  
  private static final int Q32 = -1640531527;
  
  private boolean forEncryption;
  
  public String getAlgorithmName() {
    return "RC5-32";
  }
  
  public int getBlockSize() {
    return 8;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof RC5Parameters) {
      RC5Parameters rC5Parameters = (RC5Parameters)paramCipherParameters;
      this._noRounds = rC5Parameters.getRounds();
      setKey(rC5Parameters.getKey());
    } else if (paramCipherParameters instanceof KeyParameter) {
      KeyParameter keyParameter = (KeyParameter)paramCipherParameters;
      setKey(keyParameter.getKey());
    } else {
      throw new IllegalArgumentException("invalid parameter passed to RC532 init - " + paramCipherParameters.getClass().getName());
    } 
    this.forEncryption = paramBoolean;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    return this.forEncryption ? encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2) : decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
  }
  
  public void reset() {}
  
  private void setKey(byte[] paramArrayOfbyte) {
    int[] arrayOfInt = new int[(paramArrayOfbyte.length + 3) / 4];
    int i;
    for (i = 0; i != paramArrayOfbyte.length; i++)
      arrayOfInt[i / 4] = arrayOfInt[i / 4] + ((paramArrayOfbyte[i] & 0xFF) << 8 * i % 4); 
    this._S = new int[2 * (this._noRounds + 1)];
    this._S[0] = -1209970333;
    for (i = 1; i < this._S.length; i++)
      this._S[i] = this._S[i - 1] + -1640531527; 
    if (arrayOfInt.length > this._S.length) {
      i = 3 * arrayOfInt.length;
    } else {
      i = 3 * this._S.length;
    } 
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    for (byte b = 0; b < i; b++) {
      j = this._S[m] = rotateLeft(this._S[m] + j + k, 3);
      k = arrayOfInt[n] = rotateLeft(arrayOfInt[n] + j + k, j + k);
      m = (m + 1) % this._S.length;
      n = (n + 1) % arrayOfInt.length;
    } 
  }
  
  private int encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = bytesToWord(paramArrayOfbyte1, paramInt1) + this._S[0];
    int j = bytesToWord(paramArrayOfbyte1, paramInt1 + 4) + this._S[1];
    for (byte b = 1; b <= this._noRounds; b++) {
      i = rotateLeft(i ^ j, j) + this._S[2 * b];
      j = rotateLeft(j ^ i, i) + this._S[2 * b + 1];
    } 
    wordToBytes(i, paramArrayOfbyte2, paramInt2);
    wordToBytes(j, paramArrayOfbyte2, paramInt2 + 4);
    return 8;
  }
  
  private int decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = bytesToWord(paramArrayOfbyte1, paramInt1);
    int j = bytesToWord(paramArrayOfbyte1, paramInt1 + 4);
    for (int k = this._noRounds; k >= 1; k--) {
      j = rotateRight(j - this._S[2 * k + 1], i) ^ i;
      i = rotateRight(i - this._S[2 * k], j) ^ j;
    } 
    wordToBytes(i - this._S[0], paramArrayOfbyte2, paramInt2);
    wordToBytes(j - this._S[1], paramArrayOfbyte2, paramInt2 + 4);
    return 8;
  }
  
  private int rotateLeft(int paramInt1, int paramInt2) {
    return paramInt1 << (paramInt2 & 0x1F) | paramInt1 >>> 32 - (paramInt2 & 0x1F);
  }
  
  private int rotateRight(int paramInt1, int paramInt2) {
    return paramInt1 >>> (paramInt2 & 0x1F) | paramInt1 << 32 - (paramInt2 & 0x1F);
  }
  
  private int bytesToWord(byte[] paramArrayOfbyte, int paramInt) {
    return paramArrayOfbyte[paramInt] & 0xFF | (paramArrayOfbyte[paramInt + 1] & 0xFF) << 8 | (paramArrayOfbyte[paramInt + 2] & 0xFF) << 16 | (paramArrayOfbyte[paramInt + 3] & 0xFF) << 24;
  }
  
  private void wordToBytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
    paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >> 8);
    paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >> 16);
    paramArrayOfbyte[paramInt2 + 3] = (byte)(paramInt1 >> 24);
  }
}
