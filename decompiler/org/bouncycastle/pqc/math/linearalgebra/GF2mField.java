package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

public class GF2mField {
  private int degree = 0;
  
  private int polynomial;
  
  public GF2mField(int paramInt) {
    if (paramInt >= 32)
      throw new IllegalArgumentException(" Error: the degree of field is too large "); 
    if (paramInt < 1)
      throw new IllegalArgumentException(" Error: the degree of field is non-positive "); 
    this.degree = paramInt;
    this.polynomial = PolynomialRingGF2.getIrreduciblePolynomial(paramInt);
  }
  
  public GF2mField(int paramInt1, int paramInt2) {
    if (paramInt1 != PolynomialRingGF2.degree(paramInt2))
      throw new IllegalArgumentException(" Error: the degree is not correct"); 
    if (!PolynomialRingGF2.isIrreducible(paramInt2))
      throw new IllegalArgumentException(" Error: given polynomial is reducible"); 
    this.degree = paramInt1;
    this.polynomial = paramInt2;
  }
  
  public GF2mField(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length != 4)
      throw new IllegalArgumentException("byte array is not an encoded finite field"); 
    this.polynomial = LittleEndianConversions.OS2IP(paramArrayOfbyte);
    if (!PolynomialRingGF2.isIrreducible(this.polynomial))
      throw new IllegalArgumentException("byte array is not an encoded finite field"); 
    this.degree = PolynomialRingGF2.degree(this.polynomial);
  }
  
  public GF2mField(GF2mField paramGF2mField) {
    this.degree = paramGF2mField.degree;
    this.polynomial = paramGF2mField.polynomial;
  }
  
  public int getDegree() {
    return this.degree;
  }
  
  public int getPolynomial() {
    return this.polynomial;
  }
  
  public byte[] getEncoded() {
    return LittleEndianConversions.I2OSP(this.polynomial);
  }
  
  public int add(int paramInt1, int paramInt2) {
    return paramInt1 ^ paramInt2;
  }
  
  public int mult(int paramInt1, int paramInt2) {
    return PolynomialRingGF2.modMultiply(paramInt1, paramInt2, this.polynomial);
  }
  
  public int exp(int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return 1; 
    if (paramInt1 == 0)
      return 0; 
    if (paramInt1 == 1)
      return 1; 
    int i = 1;
    if (paramInt2 < 0) {
      paramInt1 = inverse(paramInt1);
      paramInt2 = -paramInt2;
    } 
    while (paramInt2 != 0) {
      if ((paramInt2 & 0x1) == 1)
        i = mult(i, paramInt1); 
      paramInt1 = mult(paramInt1, paramInt1);
      paramInt2 >>>= 1;
    } 
    return i;
  }
  
  public int inverse(int paramInt) {
    int i = (1 << this.degree) - 2;
    return exp(paramInt, i);
  }
  
  public int sqRoot(int paramInt) {
    for (byte b = 1; b < this.degree; b++)
      paramInt = mult(paramInt, paramInt); 
    return paramInt;
  }
  
  public int getRandomElement(SecureRandom paramSecureRandom) {
    return RandUtils.nextInt(paramSecureRandom, 1 << this.degree);
  }
  
  public int getRandomNonZeroElement() {
    return getRandomNonZeroElement(new SecureRandom());
  }
  
  public int getRandomNonZeroElement(SecureRandom paramSecureRandom) {
    int i = 1048576;
    byte b = 0;
    int j = RandUtils.nextInt(paramSecureRandom, 1 << this.degree);
    while (j == 0 && b < i) {
      j = RandUtils.nextInt(paramSecureRandom, 1 << this.degree);
      b++;
    } 
    if (b == i)
      j = 1; 
    return j;
  }
  
  public boolean isElementOfThisField(int paramInt) {
    return (this.degree == 31) ? ((paramInt >= 0)) : ((paramInt >= 0 && paramInt < 1 << this.degree));
  }
  
  public String elementToStr(int paramInt) {
    String str = "";
    for (byte b = 0; b < this.degree; b++) {
      if (((byte)paramInt & 0x1) == 0) {
        str = "0" + str;
      } else {
        str = "1" + str;
      } 
      paramInt >>>= 1;
    } 
    return str;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof GF2mField))
      return false; 
    GF2mField gF2mField = (GF2mField)paramObject;
    return (this.degree == gF2mField.degree && this.polynomial == gF2mField.polynomial);
  }
  
  public int hashCode() {
    return this.polynomial;
  }
  
  public String toString() {
    return "Finite Field GF(2^" + this.degree + ") = " + "GF(2)[X]/<" + polyToString(this.polynomial) + "> ";
  }
  
  private static String polyToString(int paramInt) {
    String str = "";
    if (paramInt == 0) {
      str = "0";
    } else {
      byte b = (byte)(paramInt & 0x1);
      if (b == 1)
        str = "1"; 
      paramInt >>>= 1;
      for (byte b1 = 1; paramInt != 0; b1++) {
        b = (byte)(paramInt & 0x1);
        if (b == 1)
          str = str + "+x^" + b1; 
        paramInt >>>= 1;
      } 
    } 
    return str;
  }
}
