package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class DHPublicKeyParameters extends DHKeyParameters {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private static final BigInteger TWO = BigInteger.valueOf(2L);
  
  private BigInteger y;
  
  public DHPublicKeyParameters(BigInteger paramBigInteger, DHParameters paramDHParameters) {
    super(false, paramDHParameters);
    this.y = validate(paramBigInteger, paramDHParameters);
  }
  
  private BigInteger validate(BigInteger paramBigInteger, DHParameters paramDHParameters) {
    if (paramBigInteger == null)
      throw new NullPointerException("y value cannot be null"); 
    if (paramBigInteger.compareTo(TWO) < 0 || paramBigInteger.compareTo(paramDHParameters.getP().subtract(TWO)) > 0)
      throw new IllegalArgumentException("invalid DH public key"); 
    if (paramDHParameters.getQ() != null) {
      if (ONE.equals(paramBigInteger.modPow(paramDHParameters.getQ(), paramDHParameters.getP())))
        return paramBigInteger; 
      throw new IllegalArgumentException("Y value does not appear to be in correct group");
    } 
    return paramBigInteger;
  }
  
  public BigInteger getY() {
    return this.y;
  }
  
  public int hashCode() {
    return this.y.hashCode() ^ super.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof DHPublicKeyParameters))
      return false; 
    DHPublicKeyParameters dHPublicKeyParameters = (DHPublicKeyParameters)paramObject;
    return (dHPublicKeyParameters.getY().equals(this.y) && super.equals(paramObject));
  }
}
