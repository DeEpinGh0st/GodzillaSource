package org.bouncycastle.math.field;

import java.math.BigInteger;

public abstract class FiniteFields {
  static final FiniteField GF_2 = new PrimeField(BigInteger.valueOf(2L));
  
  static final FiniteField GF_3 = new PrimeField(BigInteger.valueOf(3L));
  
  public static PolynomialExtensionField getBinaryExtensionField(int[] paramArrayOfint) {
    if (paramArrayOfint[0] != 0)
      throw new IllegalArgumentException("Irreducible polynomials in GF(2) must have constant term"); 
    for (byte b = 1; b < paramArrayOfint.length; b++) {
      if (paramArrayOfint[b] <= paramArrayOfint[b - 1])
        throw new IllegalArgumentException("Polynomial exponents must be montonically increasing"); 
    } 
    return new GenericPolynomialExtensionField(GF_2, new GF2Polynomial(paramArrayOfint));
  }
  
  public static FiniteField getPrimeField(BigInteger paramBigInteger) {
    int i = paramBigInteger.bitLength();
    if (paramBigInteger.signum() <= 0 || i < 2)
      throw new IllegalArgumentException("'characteristic' must be >= 2"); 
    if (i < 3)
      switch (paramBigInteger.intValue()) {
        case 2:
          return GF_2;
        case 3:
          return GF_3;
      }  
    return new PrimeField(paramBigInteger);
  }
}
