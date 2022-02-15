package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

public class GF2Vector extends Vector {
  private int[] v;
  
  public GF2Vector(int paramInt) {
    if (paramInt < 0)
      throw new ArithmeticException("Negative length."); 
    this.length = paramInt;
    this.v = new int[paramInt + 31 >> 5];
  }
  
  public GF2Vector(int paramInt, SecureRandom paramSecureRandom) {
    this.length = paramInt;
    int i = paramInt + 31 >> 5;
    this.v = new int[i];
    int j;
    for (j = i - 1; j >= 0; j--)
      this.v[j] = paramSecureRandom.nextInt(); 
    j = paramInt & 0x1F;
    if (j != 0)
      this.v[i - 1] = this.v[i - 1] & (1 << j) - 1; 
  }
  
  public GF2Vector(int paramInt1, int paramInt2, SecureRandom paramSecureRandom) {
    if (paramInt2 > paramInt1)
      throw new ArithmeticException("The hamming weight is greater than the length of vector."); 
    this.length = paramInt1;
    int i = paramInt1 + 31 >> 5;
    this.v = new int[i];
    int[] arrayOfInt = new int[paramInt1];
    int j;
    for (j = 0; j < paramInt1; j++)
      arrayOfInt[j] = j; 
    j = paramInt1;
    for (byte b = 0; b < paramInt2; b++) {
      int k = RandUtils.nextInt(paramSecureRandom, j);
      setBit(arrayOfInt[k]);
      arrayOfInt[k] = arrayOfInt[--j];
    } 
  }
  
  public GF2Vector(int paramInt, int[] paramArrayOfint) {
    if (paramInt < 0)
      throw new ArithmeticException("negative length"); 
    this.length = paramInt;
    int i = paramInt + 31 >> 5;
    if (paramArrayOfint.length != i)
      throw new ArithmeticException("length mismatch"); 
    this.v = IntUtils.clone(paramArrayOfint);
    int j = paramInt & 0x1F;
    if (j != 0)
      this.v[i - 1] = this.v[i - 1] & (1 << j) - 1; 
  }
  
  public GF2Vector(GF2Vector paramGF2Vector) {
    this.length = paramGF2Vector.length;
    this.v = IntUtils.clone(paramGF2Vector.v);
  }
  
  protected GF2Vector(int[] paramArrayOfint, int paramInt) {
    this.v = paramArrayOfint;
    this.length = paramInt;
  }
  
  public static GF2Vector OS2VP(int paramInt, byte[] paramArrayOfbyte) {
    if (paramInt < 0)
      throw new ArithmeticException("negative length"); 
    int i = paramInt + 7 >> 3;
    if (paramArrayOfbyte.length > i)
      throw new ArithmeticException("length mismatch"); 
    return new GF2Vector(paramInt, LittleEndianConversions.toIntArray(paramArrayOfbyte));
  }
  
  public byte[] getEncoded() {
    int i = this.length + 7 >> 3;
    return LittleEndianConversions.toByteArray(this.v, i);
  }
  
  public int[] getVecArray() {
    return this.v;
  }
  
  public int getHammingWeight() {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.v.length; b2++) {
      int i = this.v[b2];
      for (byte b = 0; b < 32; b++) {
        int j = i & 0x1;
        if (j != 0)
          b1++; 
        i >>>= 1;
      } 
    } 
    return b1;
  }
  
  public boolean isZero() {
    for (int i = this.v.length - 1; i >= 0; i--) {
      if (this.v[i] != 0)
        return false; 
    } 
    return true;
  }
  
  public int getBit(int paramInt) {
    if (paramInt >= this.length)
      throw new IndexOutOfBoundsException(); 
    int i = paramInt >> 5;
    int j = paramInt & 0x1F;
    return (this.v[i] & 1 << j) >>> j;
  }
  
  public void setBit(int paramInt) {
    if (paramInt >= this.length)
      throw new IndexOutOfBoundsException(); 
    this.v[paramInt >> 5] = this.v[paramInt >> 5] | 1 << (paramInt & 0x1F);
  }
  
  public Vector add(Vector paramVector) {
    if (!(paramVector instanceof GF2Vector))
      throw new ArithmeticException("vector is not defined over GF(2)"); 
    GF2Vector gF2Vector = (GF2Vector)paramVector;
    if (this.length != gF2Vector.length)
      throw new ArithmeticException("length mismatch"); 
    int[] arrayOfInt = IntUtils.clone(((GF2Vector)paramVector).v);
    for (int i = arrayOfInt.length - 1; i >= 0; i--)
      arrayOfInt[i] = arrayOfInt[i] ^ this.v[i]; 
    return new GF2Vector(this.length, arrayOfInt);
  }
  
  public Vector multiply(Permutation paramPermutation) {
    int[] arrayOfInt = paramPermutation.getVector();
    if (this.length != arrayOfInt.length)
      throw new ArithmeticException("length mismatch"); 
    GF2Vector gF2Vector = new GF2Vector(this.length);
    for (byte b = 0; b < arrayOfInt.length; b++) {
      int i = this.v[arrayOfInt[b] >> 5] & 1 << (arrayOfInt[b] & 0x1F);
      if (i != 0)
        gF2Vector.v[b >> 5] = gF2Vector.v[b >> 5] | 1 << (b & 0x1F); 
    } 
    return gF2Vector;
  }
  
  public GF2Vector extractVector(int[] paramArrayOfint) {
    int i = paramArrayOfint.length;
    if (paramArrayOfint[i - 1] > this.length)
      throw new ArithmeticException("invalid index set"); 
    GF2Vector gF2Vector = new GF2Vector(i);
    for (byte b = 0; b < i; b++) {
      int j = this.v[paramArrayOfint[b] >> 5] & 1 << (paramArrayOfint[b] & 0x1F);
      if (j != 0)
        gF2Vector.v[b >> 5] = gF2Vector.v[b >> 5] | 1 << (b & 0x1F); 
    } 
    return gF2Vector;
  }
  
  public GF2Vector extractLeftVector(int paramInt) {
    if (paramInt > this.length)
      throw new ArithmeticException("invalid length"); 
    if (paramInt == this.length)
      return new GF2Vector(this); 
    GF2Vector gF2Vector = new GF2Vector(paramInt);
    int i = paramInt >> 5;
    int j = paramInt & 0x1F;
    System.arraycopy(this.v, 0, gF2Vector.v, 0, i);
    if (j != 0)
      gF2Vector.v[i] = this.v[i] & (1 << j) - 1; 
    return gF2Vector;
  }
  
  public GF2Vector extractRightVector(int paramInt) {
    if (paramInt > this.length)
      throw new ArithmeticException("invalid length"); 
    if (paramInt == this.length)
      return new GF2Vector(this); 
    GF2Vector gF2Vector = new GF2Vector(paramInt);
    int i = this.length - paramInt >> 5;
    int j = this.length - paramInt & 0x1F;
    int k = paramInt + 31 >> 5;
    int m = i;
    if (j != 0) {
      for (byte b = 0; b < k - 1; b++)
        gF2Vector.v[b] = this.v[m++] >>> j | this.v[m] << 32 - j; 
      gF2Vector.v[k - 1] = this.v[m++] >>> j;
      if (m < this.v.length)
        gF2Vector.v[k - 1] = gF2Vector.v[k - 1] | this.v[m] << 32 - j; 
    } else {
      System.arraycopy(this.v, i, gF2Vector.v, 0, k);
    } 
    return gF2Vector;
  }
  
  public GF2mVector toExtensionFieldVector(GF2mField paramGF2mField) {
    int i = paramGF2mField.getDegree();
    if (this.length % i != 0)
      throw new ArithmeticException("conversion is impossible"); 
    int j = this.length / i;
    int[] arrayOfInt = new int[j];
    byte b = 0;
    for (int k = j - 1; k >= 0; k--) {
      for (int m = paramGF2mField.getDegree() - 1; m >= 0; m--) {
        int n = b >>> 5;
        int i1 = b & 0x1F;
        int i2 = this.v[n] >>> i1 & 0x1;
        if (i2 == 1)
          arrayOfInt[k] = arrayOfInt[k] ^ 1 << m; 
        b++;
      } 
    } 
    return new GF2mVector(paramGF2mField, arrayOfInt);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof GF2Vector))
      return false; 
    GF2Vector gF2Vector = (GF2Vector)paramObject;
    return (this.length == gF2Vector.length && IntUtils.equals(this.v, gF2Vector.v));
  }
  
  public int hashCode() {
    null = this.length;
    return null * 31 + this.v.hashCode();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.length; b++) {
      if (b != 0 && (b & 0x1F) == 0)
        stringBuffer.append(' '); 
      int i = b >> 5;
      int j = b & 0x1F;
      int k = this.v[i] & 1 << j;
      if (k == 0) {
        stringBuffer.append('0');
      } else {
        stringBuffer.append('1');
      } 
    } 
    return stringBuffer.toString();
  }
}
