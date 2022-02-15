package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

public class Permutation {
  private int[] perm;
  
  public Permutation(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("invalid length"); 
    this.perm = new int[paramInt];
    for (int i = paramInt - 1; i >= 0; i--)
      this.perm[i] = i; 
  }
  
  public Permutation(int[] paramArrayOfint) {
    if (!isPermutation(paramArrayOfint))
      throw new IllegalArgumentException("array is not a permutation vector"); 
    this.perm = IntUtils.clone(paramArrayOfint);
  }
  
  public Permutation(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length <= 4)
      throw new IllegalArgumentException("invalid encoding"); 
    int i = LittleEndianConversions.OS2IP(paramArrayOfbyte, 0);
    int j = IntegerFunctions.ceilLog256(i - 1);
    if (paramArrayOfbyte.length != 4 + i * j)
      throw new IllegalArgumentException("invalid encoding"); 
    this.perm = new int[i];
    for (byte b = 0; b < i; b++)
      this.perm[b] = LittleEndianConversions.OS2IP(paramArrayOfbyte, 4 + b * j, j); 
    if (!isPermutation(this.perm))
      throw new IllegalArgumentException("invalid encoding"); 
  }
  
  public Permutation(int paramInt, SecureRandom paramSecureRandom) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("invalid length"); 
    this.perm = new int[paramInt];
    int[] arrayOfInt = new int[paramInt];
    int i;
    for (i = 0; i < paramInt; i++)
      arrayOfInt[i] = i; 
    i = paramInt;
    for (byte b = 0; b < paramInt; b++) {
      int j = RandUtils.nextInt(paramSecureRandom, i);
      i--;
      this.perm[b] = arrayOfInt[j];
      arrayOfInt[j] = arrayOfInt[i];
    } 
  }
  
  public byte[] getEncoded() {
    int i = this.perm.length;
    int j = IntegerFunctions.ceilLog256(i - 1);
    byte[] arrayOfByte = new byte[4 + i * j];
    LittleEndianConversions.I2OSP(i, arrayOfByte, 0);
    for (byte b = 0; b < i; b++)
      LittleEndianConversions.I2OSP(this.perm[b], arrayOfByte, 4 + b * j, j); 
    return arrayOfByte;
  }
  
  public int[] getVector() {
    return IntUtils.clone(this.perm);
  }
  
  public Permutation computeInverse() {
    Permutation permutation = new Permutation(this.perm.length);
    for (int i = this.perm.length - 1; i >= 0; i--)
      permutation.perm[this.perm[i]] = i; 
    return permutation;
  }
  
  public Permutation rightMultiply(Permutation paramPermutation) {
    if (paramPermutation.perm.length != this.perm.length)
      throw new IllegalArgumentException("length mismatch"); 
    Permutation permutation = new Permutation(this.perm.length);
    for (int i = this.perm.length - 1; i >= 0; i--)
      permutation.perm[i] = this.perm[paramPermutation.perm[i]]; 
    return permutation;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof Permutation))
      return false; 
    Permutation permutation = (Permutation)paramObject;
    return IntUtils.equals(this.perm, permutation.perm);
  }
  
  public String toString() {
    null = "[" + this.perm[0];
    for (byte b = 1; b < this.perm.length; b++)
      null = null + ", " + this.perm[b]; 
    return null + "]";
  }
  
  public int hashCode() {
    return this.perm.hashCode();
  }
  
  private boolean isPermutation(int[] paramArrayOfint) {
    int i = paramArrayOfint.length;
    boolean[] arrayOfBoolean = new boolean[i];
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfint[b] < 0 || paramArrayOfint[b] >= i || arrayOfBoolean[paramArrayOfint[b]])
        return false; 
      arrayOfBoolean[paramArrayOfint[b]] = true;
    } 
    return true;
  }
}
