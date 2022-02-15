package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class CramerShoupPublicKeyParameters extends CramerShoupKeyParameters {
  private BigInteger c;
  
  private BigInteger d;
  
  private BigInteger h;
  
  public CramerShoupPublicKeyParameters(CramerShoupParameters paramCramerShoupParameters, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    super(false, paramCramerShoupParameters);
    this.c = paramBigInteger1;
    this.d = paramBigInteger2;
    this.h = paramBigInteger3;
  }
  
  public BigInteger getC() {
    return this.c;
  }
  
  public BigInteger getD() {
    return this.d;
  }
  
  public BigInteger getH() {
    return this.h;
  }
  
  public int hashCode() {
    return this.c.hashCode() ^ this.d.hashCode() ^ this.h.hashCode() ^ super.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof CramerShoupPublicKeyParameters))
      return false; 
    CramerShoupPublicKeyParameters cramerShoupPublicKeyParameters = (CramerShoupPublicKeyParameters)paramObject;
    return (cramerShoupPublicKeyParameters.getC().equals(this.c) && cramerShoupPublicKeyParameters.getD().equals(this.d) && cramerShoupPublicKeyParameters.getH().equals(this.h) && super.equals(paramObject));
  }
}
