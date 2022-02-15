package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public class IDEAEngine implements BlockCipher {
  protected static final int BLOCK_SIZE = 8;
  
  private int[] workingKey = null;
  
  private static final int MASK = 65535;
  
  private static final int BASE = 65537;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof KeyParameter) {
      this.workingKey = generateWorkingKey(paramBoolean, ((KeyParameter)paramCipherParameters).getKey());
      return;
    } 
    throw new IllegalArgumentException("invalid parameter passed to IDEA init - " + paramCipherParameters.getClass().getName());
  }
  
  public String getAlgorithmName() {
    return "IDEA";
  }
  
  public int getBlockSize() {
    return 8;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    if (this.workingKey == null)
      throw new IllegalStateException("IDEA engine not initialised"); 
    if (paramInt1 + 8 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + 8 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    ideaFunc(this.workingKey, paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
    return 8;
  }
  
  public void reset() {}
  
  private int bytesToWord(byte[] paramArrayOfbyte, int paramInt) {
    return (paramArrayOfbyte[paramInt] << 8 & 0xFF00) + (paramArrayOfbyte[paramInt + 1] & 0xFF);
  }
  
  private void wordToBytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[paramInt2 + 1] = (byte)paramInt1;
  }
  
  private int mul(int paramInt1, int paramInt2) {
    if (paramInt1 == 0) {
      paramInt1 = 65537 - paramInt2;
    } else if (paramInt2 == 0) {
      paramInt1 = 65537 - paramInt1;
    } else {
      int i = paramInt1 * paramInt2;
      paramInt2 = i & 0xFFFF;
      paramInt1 = i >>> 16;
      paramInt1 = paramInt2 - paramInt1 + ((paramInt2 < paramInt1) ? 1 : 0);
    } 
    return paramInt1 & 0xFFFF;
  }
  
  private void ideaFunc(int[] paramArrayOfint, byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    byte b1 = 0;
    int i = bytesToWord(paramArrayOfbyte1, paramInt1);
    int j = bytesToWord(paramArrayOfbyte1, paramInt1 + 2);
    int k = bytesToWord(paramArrayOfbyte1, paramInt1 + 4);
    int m = bytesToWord(paramArrayOfbyte1, paramInt1 + 6);
    for (byte b2 = 0; b2 < 8; b2++) {
      i = mul(i, paramArrayOfint[b1++]);
      j += paramArrayOfint[b1++];
      j &= 0xFFFF;
      k += paramArrayOfint[b1++];
      k &= 0xFFFF;
      m = mul(m, paramArrayOfint[b1++]);
      int n = j;
      int i1 = k;
      k ^= i;
      j ^= m;
      k = mul(k, paramArrayOfint[b1++]);
      j += k;
      j &= 0xFFFF;
      j = mul(j, paramArrayOfint[b1++]);
      k += j;
      k &= 0xFFFF;
      i ^= j;
      m ^= k;
      j ^= i1;
      k ^= n;
    } 
    wordToBytes(mul(i, paramArrayOfint[b1++]), paramArrayOfbyte2, paramInt2);
    wordToBytes(k + paramArrayOfint[b1++], paramArrayOfbyte2, paramInt2 + 2);
    wordToBytes(j + paramArrayOfint[b1++], paramArrayOfbyte2, paramInt2 + 4);
    wordToBytes(mul(m, paramArrayOfint[b1]), paramArrayOfbyte2, paramInt2 + 6);
  }
  
  private int[] expandKey(byte[] paramArrayOfbyte) {
    int[] arrayOfInt = new int[52];
    if (paramArrayOfbyte.length < 16) {
      byte[] arrayOfByte = new byte[16];
      System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, arrayOfByte.length - paramArrayOfbyte.length, paramArrayOfbyte.length);
      paramArrayOfbyte = arrayOfByte;
    } 
    byte b;
    for (b = 0; b < 8; b++)
      arrayOfInt[b] = bytesToWord(paramArrayOfbyte, b * 2); 
    for (b = 8; b < 52; b++) {
      if ((b & 0x7) < 6) {
        arrayOfInt[b] = ((arrayOfInt[b - 7] & 0x7F) << 9 | arrayOfInt[b - 6] >> 7) & 0xFFFF;
      } else if ((b & 0x7) == 6) {
        arrayOfInt[b] = ((arrayOfInt[b - 7] & 0x7F) << 9 | arrayOfInt[b - 14] >> 7) & 0xFFFF;
      } else {
        arrayOfInt[b] = ((arrayOfInt[b - 15] & 0x7F) << 9 | arrayOfInt[b - 14] >> 7) & 0xFFFF;
      } 
    } 
    return arrayOfInt;
  }
  
  private int mulInv(int paramInt) {
    if (paramInt < 2)
      return paramInt; 
    int i = 1;
    int j = 65537 / paramInt;
    int k = 65537 % paramInt;
    while (k != 1) {
      int m = paramInt / k;
      paramInt %= k;
      i = i + j * m & 0xFFFF;
      if (paramInt == 1)
        return i; 
      m = k / paramInt;
      k %= paramInt;
      j = j + i * m & 0xFFFF;
    } 
    return 1 - j & 0xFFFF;
  }
  
  int addInv(int paramInt) {
    return 0 - paramInt & 0xFFFF;
  }
  
  private int[] invertKey(int[] paramArrayOfint) {
    byte b1 = 52;
    int[] arrayOfInt = new int[52];
    byte b2 = 0;
    int i = mulInv(paramArrayOfint[b2++]);
    int j = addInv(paramArrayOfint[b2++]);
    int k = addInv(paramArrayOfint[b2++]);
    int m = mulInv(paramArrayOfint[b2++]);
    arrayOfInt[--b1] = m;
    arrayOfInt[--b1] = k;
    arrayOfInt[--b1] = j;
    arrayOfInt[--b1] = i;
    for (byte b3 = 1; b3 < 8; b3++) {
      i = paramArrayOfint[b2++];
      j = paramArrayOfint[b2++];
      arrayOfInt[--b1] = j;
      arrayOfInt[--b1] = i;
      i = mulInv(paramArrayOfint[b2++]);
      j = addInv(paramArrayOfint[b2++]);
      k = addInv(paramArrayOfint[b2++]);
      m = mulInv(paramArrayOfint[b2++]);
      arrayOfInt[--b1] = m;
      arrayOfInt[--b1] = j;
      arrayOfInt[--b1] = k;
      arrayOfInt[--b1] = i;
    } 
    i = paramArrayOfint[b2++];
    j = paramArrayOfint[b2++];
    arrayOfInt[--b1] = j;
    arrayOfInt[--b1] = i;
    i = mulInv(paramArrayOfint[b2++]);
    j = addInv(paramArrayOfint[b2++]);
    k = addInv(paramArrayOfint[b2++]);
    m = mulInv(paramArrayOfint[b2]);
    arrayOfInt[--b1] = m;
    arrayOfInt[--b1] = k;
    arrayOfInt[--b1] = j;
    arrayOfInt[--b1] = i;
    return arrayOfInt;
  }
  
  private int[] generateWorkingKey(boolean paramBoolean, byte[] paramArrayOfbyte) {
    return paramBoolean ? expandKey(paramArrayOfbyte) : invertKey(expandKey(paramArrayOfbyte));
  }
}
