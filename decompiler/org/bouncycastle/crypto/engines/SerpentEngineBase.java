package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public abstract class SerpentEngineBase implements BlockCipher {
  protected static final int BLOCK_SIZE = 16;
  
  static final int ROUNDS = 32;
  
  static final int PHI = -1640531527;
  
  protected boolean encrypting;
  
  protected int[] wKey;
  
  protected int X0;
  
  protected int X1;
  
  protected int X2;
  
  protected int X3;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof KeyParameter) {
      this.encrypting = paramBoolean;
      this.wKey = makeWorkingKey(((KeyParameter)paramCipherParameters).getKey());
      return;
    } 
    throw new IllegalArgumentException("invalid parameter passed to " + getAlgorithmName() + " init - " + paramCipherParameters.getClass().getName());
  }
  
  public String getAlgorithmName() {
    return "Serpent";
  }
  
  public int getBlockSize() {
    return 16;
  }
  
  public final int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    if (this.wKey == null)
      throw new IllegalStateException(getAlgorithmName() + " not initialised"); 
    if (paramInt1 + 16 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + 16 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    if (this.encrypting) {
      encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
    } else {
      decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
    } 
    return 16;
  }
  
  public void reset() {}
  
  protected static int rotateLeft(int paramInt1, int paramInt2) {
    return paramInt1 << paramInt2 | paramInt1 >>> -paramInt2;
  }
  
  protected static int rotateRight(int paramInt1, int paramInt2) {
    return paramInt1 >>> paramInt2 | paramInt1 << -paramInt2;
  }
  
  protected final void sb0(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt1 ^ paramInt4;
    int j = paramInt3 ^ i;
    int k = paramInt2 ^ j;
    this.X3 = paramInt1 & paramInt4 ^ k;
    int m = paramInt1 ^ paramInt2 & i;
    this.X2 = k ^ (paramInt3 | m);
    int n = this.X3 & (j ^ m);
    this.X1 = j ^ 0xFFFFFFFF ^ n;
    this.X0 = n ^ m ^ 0xFFFFFFFF;
  }
  
  protected final void ib0(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt1 ^ 0xFFFFFFFF;
    int j = paramInt1 ^ paramInt2;
    int k = paramInt4 ^ (i | j);
    int m = paramInt3 ^ k;
    this.X2 = j ^ m;
    int n = i ^ paramInt4 & j;
    this.X1 = k ^ this.X2 & n;
    this.X3 = paramInt1 & k ^ (m | this.X1);
    this.X0 = this.X3 ^ m ^ n;
  }
  
  protected final void sb1(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt2 ^ paramInt1 ^ 0xFFFFFFFF;
    int j = paramInt3 ^ (paramInt1 | i);
    this.X2 = paramInt4 ^ j;
    int k = paramInt2 ^ (paramInt4 | i);
    int m = i ^ this.X2;
    this.X3 = m ^ j & k;
    int n = j ^ k;
    this.X1 = this.X3 ^ n;
    this.X0 = j ^ m & n;
  }
  
  protected final void ib1(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt2 ^ paramInt4;
    int j = paramInt1 ^ paramInt2 & i;
    int k = i ^ j;
    this.X3 = paramInt3 ^ k;
    int m = paramInt2 ^ i & j;
    int n = this.X3 | m;
    this.X1 = j ^ n;
    int i1 = this.X1 ^ 0xFFFFFFFF;
    int i2 = this.X3 ^ m;
    this.X0 = i1 ^ i2;
    this.X2 = k ^ (i1 | i2);
  }
  
  protected final void sb2(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt1 ^ 0xFFFFFFFF;
    int j = paramInt2 ^ paramInt4;
    int k = paramInt3 & i;
    this.X0 = j ^ k;
    int m = paramInt3 ^ i;
    int n = paramInt3 ^ this.X0;
    int i1 = paramInt2 & n;
    this.X3 = m ^ i1;
    this.X2 = paramInt1 ^ (paramInt4 | i1) & (this.X0 | m);
    this.X1 = j ^ this.X3 ^ this.X2 ^ (paramInt4 | i);
  }
  
  protected final void ib2(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt2 ^ paramInt4;
    int j = i ^ 0xFFFFFFFF;
    int k = paramInt1 ^ paramInt3;
    int m = paramInt3 ^ i;
    int n = paramInt2 & m;
    this.X0 = k ^ n;
    int i1 = paramInt1 | j;
    int i2 = paramInt4 ^ i1;
    int i3 = k | i2;
    this.X3 = i ^ i3;
    int i4 = m ^ 0xFFFFFFFF;
    int i5 = this.X0 | this.X3;
    this.X1 = i4 ^ i5;
    this.X2 = paramInt4 & i4 ^ k ^ i5;
  }
  
  protected final void sb3(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt1 ^ paramInt2;
    int j = paramInt1 & paramInt3;
    int k = paramInt1 | paramInt4;
    int m = paramInt3 ^ paramInt4;
    int n = i & k;
    int i1 = j | n;
    this.X2 = m ^ i1;
    int i2 = paramInt2 ^ k;
    int i3 = i1 ^ i2;
    int i4 = m & i3;
    this.X0 = i ^ i4;
    int i5 = this.X2 & this.X0;
    this.X1 = i3 ^ i5;
    this.X3 = (paramInt2 | paramInt4) ^ m ^ i5;
  }
  
  protected final void ib3(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt1 | paramInt2;
    int j = paramInt2 ^ paramInt3;
    int k = paramInt2 & j;
    int m = paramInt1 ^ k;
    int n = paramInt3 ^ m;
    int i1 = paramInt4 | m;
    this.X0 = j ^ i1;
    int i2 = j | i1;
    int i3 = paramInt4 ^ i2;
    this.X2 = n ^ i3;
    int i4 = i ^ i3;
    int i5 = this.X0 & i4;
    this.X3 = m ^ i5;
    this.X1 = this.X3 ^ this.X0 ^ i4;
  }
  
  protected final void sb4(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt1 ^ paramInt4;
    int j = paramInt4 & i;
    int k = paramInt3 ^ j;
    int m = paramInt2 | k;
    this.X3 = i ^ m;
    int n = paramInt2 ^ 0xFFFFFFFF;
    int i1 = i | n;
    this.X0 = k ^ i1;
    int i2 = paramInt1 & this.X0;
    int i3 = i ^ n;
    int i4 = m & i3;
    this.X2 = i2 ^ i4;
    this.X1 = paramInt1 ^ k ^ i3 & this.X2;
  }
  
  protected final void ib4(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt3 | paramInt4;
    int j = paramInt1 & i;
    int k = paramInt2 ^ j;
    int m = paramInt1 & k;
    int n = paramInt3 ^ m;
    this.X1 = paramInt4 ^ n;
    int i1 = paramInt1 ^ 0xFFFFFFFF;
    int i2 = n & this.X1;
    this.X3 = k ^ i2;
    int i3 = this.X1 | i1;
    int i4 = paramInt4 ^ i3;
    this.X0 = this.X3 ^ i4;
    this.X2 = k & i4 ^ this.X1 ^ i1;
  }
  
  protected final void sb5(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt1 ^ 0xFFFFFFFF;
    int j = paramInt1 ^ paramInt2;
    int k = paramInt1 ^ paramInt4;
    int m = paramInt3 ^ i;
    int n = j | k;
    this.X0 = m ^ n;
    int i1 = paramInt4 & this.X0;
    int i2 = j ^ this.X0;
    this.X1 = i1 ^ i2;
    int i3 = i | this.X0;
    int i4 = j | i1;
    int i5 = k ^ i3;
    this.X2 = i4 ^ i5;
    this.X3 = paramInt2 ^ i1 ^ this.X1 & i5;
  }
  
  protected final void ib5(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt3 ^ 0xFFFFFFFF;
    int j = paramInt2 & i;
    int k = paramInt4 ^ j;
    int m = paramInt1 & k;
    int n = paramInt2 ^ i;
    this.X3 = m ^ n;
    int i1 = paramInt2 | this.X3;
    int i2 = paramInt1 & i1;
    this.X1 = k ^ i2;
    int i3 = paramInt1 | paramInt4;
    int i4 = i ^ i1;
    this.X0 = i3 ^ i4;
    this.X2 = paramInt2 & i3 ^ (m | paramInt1 ^ paramInt3);
  }
  
  protected final void sb6(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt1 ^ 0xFFFFFFFF;
    int j = paramInt1 ^ paramInt4;
    int k = paramInt2 ^ j;
    int m = i | j;
    int n = paramInt3 ^ m;
    this.X1 = paramInt2 ^ n;
    int i1 = j | this.X1;
    int i2 = paramInt4 ^ i1;
    int i3 = n & i2;
    this.X2 = k ^ i3;
    int i4 = n ^ i2;
    this.X0 = this.X2 ^ i4;
    this.X3 = n ^ 0xFFFFFFFF ^ k & i4;
  }
  
  protected final void ib6(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt1 ^ 0xFFFFFFFF;
    int j = paramInt1 ^ paramInt2;
    int k = paramInt3 ^ j;
    int m = paramInt3 | i;
    int n = paramInt4 ^ m;
    this.X1 = k ^ n;
    int i1 = k & n;
    int i2 = j ^ i1;
    int i3 = paramInt2 | i2;
    this.X3 = n ^ i3;
    int i4 = paramInt2 | this.X3;
    this.X0 = i2 ^ i4;
    this.X2 = paramInt4 & i ^ k ^ i4;
  }
  
  protected final void sb7(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt2 ^ paramInt3;
    int j = paramInt3 & i;
    int k = paramInt4 ^ j;
    int m = paramInt1 ^ k;
    int n = paramInt4 | i;
    int i1 = m & n;
    this.X1 = paramInt2 ^ i1;
    int i2 = k | this.X1;
    int i3 = paramInt1 & m;
    this.X3 = i ^ i3;
    int i4 = m ^ i2;
    int i5 = this.X3 & i4;
    this.X2 = k ^ i5;
    this.X0 = i4 ^ 0xFFFFFFFF ^ this.X3 & this.X2;
  }
  
  protected final void ib7(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt3 | paramInt1 & paramInt2;
    int j = paramInt4 & (paramInt1 | paramInt2);
    this.X3 = i ^ j;
    int k = paramInt4 ^ 0xFFFFFFFF;
    int m = paramInt2 ^ j;
    int n = m | this.X3 ^ k;
    this.X1 = paramInt1 ^ n;
    this.X0 = paramInt3 ^ m ^ (paramInt4 | this.X1);
    this.X2 = i ^ this.X1 ^ this.X0 ^ paramInt1 & this.X3;
  }
  
  protected final void LT() {
    int i = rotateLeft(this.X0, 13);
    int j = rotateLeft(this.X2, 3);
    int k = this.X1 ^ i ^ j;
    int m = this.X3 ^ j ^ i << 3;
    this.X1 = rotateLeft(k, 1);
    this.X3 = rotateLeft(m, 7);
    this.X0 = rotateLeft(i ^ this.X1 ^ this.X3, 5);
    this.X2 = rotateLeft(j ^ this.X3 ^ this.X1 << 7, 22);
  }
  
  protected final void inverseLT() {
    int i = rotateRight(this.X2, 22) ^ this.X3 ^ this.X1 << 7;
    int j = rotateRight(this.X0, 5) ^ this.X1 ^ this.X3;
    int k = rotateRight(this.X3, 7);
    int m = rotateRight(this.X1, 1);
    this.X3 = k ^ i ^ j << 3;
    this.X1 = m ^ j ^ i;
    this.X2 = rotateRight(i, 3);
    this.X0 = rotateRight(j, 13);
  }
  
  protected abstract int[] makeWorkingKey(byte[] paramArrayOfbyte);
  
  protected abstract void encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2);
  
  protected abstract void decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2);
}
