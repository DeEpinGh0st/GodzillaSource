package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import java.security.spec.KeySpec;

public class GOST3410PublicKeySpec implements KeySpec {
  private BigInteger y;
  
  private BigInteger p;
  
  private BigInteger q;
  
  private BigInteger a;
  
  public GOST3410PublicKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) {
    this.y = paramBigInteger1;
    this.p = paramBigInteger2;
    this.q = paramBigInteger3;
    this.a = paramBigInteger4;
  }
  
  public BigInteger getY() {
    return this.y;
  }
  
  public BigInteger getP() {
    return this.p;
  }
  
  public BigInteger getQ() {
    return this.q;
  }
  
  public BigInteger getA() {
    return this.a;
  }
}
