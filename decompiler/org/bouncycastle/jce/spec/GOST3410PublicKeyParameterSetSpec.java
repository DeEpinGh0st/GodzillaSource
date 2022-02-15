package org.bouncycastle.jce.spec;

import java.math.BigInteger;

public class GOST3410PublicKeyParameterSetSpec {
  private BigInteger p;
  
  private BigInteger q;
  
  private BigInteger a;
  
  public GOST3410PublicKeyParameterSetSpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    this.p = paramBigInteger1;
    this.q = paramBigInteger2;
    this.a = paramBigInteger3;
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
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof GOST3410PublicKeyParameterSetSpec) {
      GOST3410PublicKeyParameterSetSpec gOST3410PublicKeyParameterSetSpec = (GOST3410PublicKeyParameterSetSpec)paramObject;
      return (this.a.equals(gOST3410PublicKeyParameterSetSpec.a) && this.p.equals(gOST3410PublicKeyParameterSetSpec.p) && this.q.equals(gOST3410PublicKeyParameterSetSpec.q));
    } 
    return false;
  }
  
  public int hashCode() {
    return this.a.hashCode() ^ this.p.hashCode() ^ this.q.hashCode();
  }
}
