package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class CramerShoupPrivateKeyParameters extends CramerShoupKeyParameters {
  private BigInteger x1;
  
  private BigInteger x2;
  
  private BigInteger y1;
  
  private BigInteger y2;
  
  private BigInteger z;
  
  private CramerShoupPublicKeyParameters pk;
  
  public CramerShoupPrivateKeyParameters(CramerShoupParameters paramCramerShoupParameters, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5) {
    super(true, paramCramerShoupParameters);
    this.x1 = paramBigInteger1;
    this.x2 = paramBigInteger2;
    this.y1 = paramBigInteger3;
    this.y2 = paramBigInteger4;
    this.z = paramBigInteger5;
  }
  
  public BigInteger getX1() {
    return this.x1;
  }
  
  public BigInteger getX2() {
    return this.x2;
  }
  
  public BigInteger getY1() {
    return this.y1;
  }
  
  public BigInteger getY2() {
    return this.y2;
  }
  
  public BigInteger getZ() {
    return this.z;
  }
  
  public void setPk(CramerShoupPublicKeyParameters paramCramerShoupPublicKeyParameters) {
    this.pk = paramCramerShoupPublicKeyParameters;
  }
  
  public CramerShoupPublicKeyParameters getPk() {
    return this.pk;
  }
  
  public int hashCode() {
    return this.x1.hashCode() ^ this.x2.hashCode() ^ this.y1.hashCode() ^ this.y2.hashCode() ^ this.z.hashCode() ^ super.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof CramerShoupPrivateKeyParameters))
      return false; 
    CramerShoupPrivateKeyParameters cramerShoupPrivateKeyParameters = (CramerShoupPrivateKeyParameters)paramObject;
    return (cramerShoupPrivateKeyParameters.getX1().equals(this.x1) && cramerShoupPrivateKeyParameters.getX2().equals(this.x2) && cramerShoupPrivateKeyParameters.getY1().equals(this.y1) && cramerShoupPrivateKeyParameters.getY2().equals(this.y2) && cramerShoupPrivateKeyParameters.getZ().equals(this.z) && super.equals(paramObject));
  }
}
