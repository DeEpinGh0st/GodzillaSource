package org.bouncycastle.pqc.math.linearalgebra;

public class GF2mVector extends Vector {
  private GF2mField field;
  
  private int[] vector;
  
  public GF2mVector(GF2mField paramGF2mField, byte[] paramArrayOfbyte) {
    this.field = new GF2mField(paramGF2mField);
    byte b1 = 8;
    byte b2 = 1;
    while (paramGF2mField.getDegree() > b1) {
      b2++;
      b1 += 8;
    } 
    if (paramArrayOfbyte.length % b2 != 0)
      throw new IllegalArgumentException("Byte array is not an encoded vector over the given finite field."); 
    this.length = paramArrayOfbyte.length / b2;
    this.vector = new int[this.length];
    b2 = 0;
    for (byte b3 = 0; b3 < this.vector.length; b3++) {
      for (byte b = 0; b < b1; b += 8)
        this.vector[b3] = this.vector[b3] | (paramArrayOfbyte[b2++] & 0xFF) << b; 
      if (!paramGF2mField.isElementOfThisField(this.vector[b3]))
        throw new IllegalArgumentException("Byte array is not an encoded vector over the given finite field."); 
    } 
  }
  
  public GF2mVector(GF2mField paramGF2mField, int[] paramArrayOfint) {
    this.field = paramGF2mField;
    this.length = paramArrayOfint.length;
    for (int i = paramArrayOfint.length - 1; i >= 0; i--) {
      if (!paramGF2mField.isElementOfThisField(paramArrayOfint[i]))
        throw new ArithmeticException("Element array is not specified over the given finite field."); 
    } 
    this.vector = IntUtils.clone(paramArrayOfint);
  }
  
  public GF2mVector(GF2mVector paramGF2mVector) {
    this.field = new GF2mField(paramGF2mVector.field);
    this.length = paramGF2mVector.length;
    this.vector = IntUtils.clone(paramGF2mVector.vector);
  }
  
  public GF2mField getField() {
    return this.field;
  }
  
  public int[] getIntArrayForm() {
    return IntUtils.clone(this.vector);
  }
  
  public byte[] getEncoded() {
    byte b1 = 8;
    byte b2 = 1;
    while (this.field.getDegree() > b1) {
      b2++;
      b1 += 8;
    } 
    byte[] arrayOfByte = new byte[this.vector.length * b2];
    b2 = 0;
    for (byte b3 = 0; b3 < this.vector.length; b3++) {
      for (byte b = 0; b < b1; b += 8)
        arrayOfByte[b2++] = (byte)(this.vector[b3] >>> b); 
    } 
    return arrayOfByte;
  }
  
  public boolean isZero() {
    for (int i = this.vector.length - 1; i >= 0; i--) {
      if (this.vector[i] != 0)
        return false; 
    } 
    return true;
  }
  
  public Vector add(Vector paramVector) {
    throw new RuntimeException("not implemented");
  }
  
  public Vector multiply(Permutation paramPermutation) {
    int[] arrayOfInt1 = paramPermutation.getVector();
    if (this.length != arrayOfInt1.length)
      throw new ArithmeticException("permutation size and vector size mismatch"); 
    int[] arrayOfInt2 = new int[this.length];
    for (byte b = 0; b < arrayOfInt1.length; b++)
      arrayOfInt2[b] = this.vector[arrayOfInt1[b]]; 
    return new GF2mVector(this.field, arrayOfInt2);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof GF2mVector))
      return false; 
    GF2mVector gF2mVector = (GF2mVector)paramObject;
    return !this.field.equals(gF2mVector.field) ? false : IntUtils.equals(this.vector, gF2mVector.vector);
  }
  
  public int hashCode() {
    null = this.field.hashCode();
    return null * 31 + this.vector.hashCode();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.vector.length; b++) {
      for (byte b1 = 0; b1 < this.field.getDegree(); b1++) {
        int i = b1 & 0x1F;
        int j = 1 << i;
        int k = this.vector[b] & j;
        if (k != 0) {
          stringBuffer.append('1');
        } else {
          stringBuffer.append('0');
        } 
      } 
      stringBuffer.append(' ');
    } 
    return stringBuffer.toString();
  }
}
