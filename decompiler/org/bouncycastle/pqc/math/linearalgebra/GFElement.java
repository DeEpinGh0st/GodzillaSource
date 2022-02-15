package org.bouncycastle.pqc.math.linearalgebra;

import java.math.BigInteger;

public interface GFElement {
  Object clone();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  boolean isZero();
  
  boolean isOne();
  
  GFElement add(GFElement paramGFElement) throws RuntimeException;
  
  void addToThis(GFElement paramGFElement) throws RuntimeException;
  
  GFElement subtract(GFElement paramGFElement) throws RuntimeException;
  
  void subtractFromThis(GFElement paramGFElement);
  
  GFElement multiply(GFElement paramGFElement) throws RuntimeException;
  
  void multiplyThisBy(GFElement paramGFElement) throws RuntimeException;
  
  GFElement invert() throws ArithmeticException;
  
  BigInteger toFlexiBigInt();
  
  byte[] toByteArray();
  
  String toString();
  
  String toString(int paramInt);
}
