package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class ElGamalPrivateKeyParameters extends ElGamalKeyParameters {
  private BigInteger x;
  
  public ElGamalPrivateKeyParameters(BigInteger paramBigInteger, ElGamalParameters paramElGamalParameters) {
    super(true, paramElGamalParameters);
    this.x = paramBigInteger;
  }
  
  public BigInteger getX() {
    return this.x;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof ElGamalPrivateKeyParameters))
      return false; 
    ElGamalPrivateKeyParameters elGamalPrivateKeyParameters = (ElGamalPrivateKeyParameters)paramObject;
    return !elGamalPrivateKeyParameters.getX().equals(this.x) ? false : super.equals(paramObject);
  }
  
  public int hashCode() {
    return getX().hashCode();
  }
}
