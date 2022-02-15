package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public class RC6Engine implements BlockCipher {
  private static final int wordSize = 32;
  
  private static final int bytesPerWord = 4;
  
  private static final int _noRounds = 20;
  
  private int[] _S = null;
  
  private static final int P32 = -1209970333;
  
  private static final int Q32 = -1640531527;
  
  private static final int LGW = 5;
  
  private boolean forEncryption;
  
  public String getAlgorithmName() {
    return "RC6";
  }
  
  public int getBlockSize() {
    return 16;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (!(paramCipherParameters instanceof KeyParameter))
      throw new IllegalArgumentException("invalid parameter passed to RC6 init - " + paramCipherParameters.getClass().getName()); 
    KeyParameter keyParameter = (KeyParameter)paramCipherParameters;
    this.forEncryption = paramBoolean;
    setKey(keyParameter.getKey());
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = getBlockSize();
    if (this._S == null)
      throw new IllegalStateException("RC6 engine not initialised"); 
    if (paramInt1 + i > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + i > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    return this.forEncryption ? encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2) : decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
  }
  
  public void reset() {}
  
  private void setKey(byte[] paramArrayOfbyte) {
    int i = (paramArrayOfbyte.length + 3) / 4;
    if (i == 0)
      i = 1; 
    int[] arrayOfInt = new int[(paramArrayOfbyte.length + 4 - 1) / 4];
    int j;
    for (j = paramArrayOfbyte.length - 1; j >= 0; j--)
      arrayOfInt[j / 4] = (arrayOfInt[j / 4] << 8) + (paramArrayOfbyte[j] & 0xFF); 
    this._S = new int[44];
    this._S[0] = -1209970333;
    for (j = 1; j < this._S.length; j++)
      this._S[j] = this._S[j - 1] + -1640531527; 
    if (arrayOfInt.length > this._S.length) {
      j = 3 * arrayOfInt.length;
    } else {
      j = 3 * this._S.length;
    } 
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    for (byte b = 0; b < j; b++) {
      k = this._S[n] = rotateLeft(this._S[n] + k + m, 3);
      m = arrayOfInt[i1] = rotateLeft(arrayOfInt[i1] + k + m, k + m);
      n = (n + 1) % this._S.length;
      i1 = (i1 + 1) % arrayOfInt.length;
    } 
  }
  
  private int encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = bytesToWord(paramArrayOfbyte1, paramInt1);
    int j = bytesToWord(paramArrayOfbyte1, paramInt1 + 4);
    int k = bytesToWord(paramArrayOfbyte1, paramInt1 + 8);
    int m = bytesToWord(paramArrayOfbyte1, paramInt1 + 12);
    j += this._S[0];
    m += this._S[1];
    for (byte b = 1; b <= 20; b++) {
      int n = 0;
      int i1 = 0;
      n = j * (2 * j + 1);
      n = rotateLeft(n, 5);
      i1 = m * (2 * m + 1);
      i1 = rotateLeft(i1, 5);
      i ^= n;
      i = rotateLeft(i, i1);
      i += this._S[2 * b];
      k ^= i1;
      k = rotateLeft(k, n);
      k += this._S[2 * b + 1];
      int i2 = i;
      i = j;
      j = k;
      k = m;
      m = i2;
    } 
    i += this._S[42];
    k += this._S[43];
    wordToBytes(i, paramArrayOfbyte2, paramInt2);
    wordToBytes(j, paramArrayOfbyte2, paramInt2 + 4);
    wordToBytes(k, paramArrayOfbyte2, paramInt2 + 8);
    wordToBytes(m, paramArrayOfbyte2, paramInt2 + 12);
    return 16;
  }
  
  private int decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = bytesToWord(paramArrayOfbyte1, paramInt1);
    int j = bytesToWord(paramArrayOfbyte1, paramInt1 + 4);
    int k = bytesToWord(paramArrayOfbyte1, paramInt1 + 8);
    int m = bytesToWord(paramArrayOfbyte1, paramInt1 + 12);
    k -= this._S[43];
    i -= this._S[42];
    for (byte b = 20; b >= 1; b--) {
      int n = 0;
      int i1 = 0;
      int i2 = m;
      m = k;
      k = j;
      j = i;
      i = i2;
      n = j * (2 * j + 1);
      n = rotateLeft(n, 5);
      i1 = m * (2 * m + 1);
      i1 = rotateLeft(i1, 5);
      k -= this._S[2 * b + 1];
      k = rotateRight(k, n);
      k ^= i1;
      i -= this._S[2 * b];
      i = rotateRight(i, i1);
      i ^= n;
    } 
    m -= this._S[1];
    j -= this._S[0];
    wordToBytes(i, paramArrayOfbyte2, paramInt2);
    wordToBytes(j, paramArrayOfbyte2, paramInt2 + 4);
    wordToBytes(k, paramArrayOfbyte2, paramInt2 + 8);
    wordToBytes(m, paramArrayOfbyte2, paramInt2 + 12);
    return 16;
  }
  
  private int rotateLeft(int paramInt1, int paramInt2) {
    return paramInt1 << paramInt2 | paramInt1 >>> -paramInt2;
  }
  
  private int rotateRight(int paramInt1, int paramInt2) {
    return paramInt1 >>> paramInt2 | paramInt1 << -paramInt2;
  }
  
  private int bytesToWord(byte[] paramArrayOfbyte, int paramInt) {
    int i = 0;
    for (byte b = 3; b >= 0; b--)
      i = (i << 8) + (paramArrayOfbyte[b + paramInt] & 0xFF); 
    return i;
  }
  
  private void wordToBytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    for (byte b = 0; b < 4; b++) {
      paramArrayOfbyte[b + paramInt2] = (byte)paramInt1;
      paramInt1 >>>= 8;
    } 
  }
}
