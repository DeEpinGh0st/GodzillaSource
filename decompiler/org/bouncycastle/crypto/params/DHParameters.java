package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

public class DHParameters implements CipherParameters {
  private static final int DEFAULT_MINIMUM_LENGTH = 160;
  
  private BigInteger g;
  
  private BigInteger p;
  
  private BigInteger q;
  
  private BigInteger j;
  
  private int m;
  
  private int l;
  
  private DHValidationParameters validation;
  
  private static int getDefaultMParam(int paramInt) {
    return (paramInt == 0) ? 160 : ((paramInt < 160) ? paramInt : 160);
  }
  
  public DHParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this(paramBigInteger1, paramBigInteger2, null, 0);
  }
  
  public DHParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    this(paramBigInteger1, paramBigInteger2, paramBigInteger3, 0);
  }
  
  public DHParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, int paramInt) {
    this(paramBigInteger1, paramBigInteger2, paramBigInteger3, getDefaultMParam(paramInt), paramInt, null, null);
  }
  
  public DHParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, int paramInt1, int paramInt2) {
    this(paramBigInteger1, paramBigInteger2, paramBigInteger3, paramInt1, paramInt2, null, null);
  }
  
  public DHParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, DHValidationParameters paramDHValidationParameters) {
    this(paramBigInteger1, paramBigInteger2, paramBigInteger3, 160, 0, paramBigInteger4, paramDHValidationParameters);
  }
  
  public DHParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, int paramInt1, int paramInt2, BigInteger paramBigInteger4, DHValidationParameters paramDHValidationParameters) {
    if (paramInt2 != 0) {
      if (paramInt2 > paramBigInteger1.bitLength())
        throw new IllegalArgumentException("when l value specified, it must satisfy 2^(l-1) <= p"); 
      if (paramInt2 < paramInt1)
        throw new IllegalArgumentException("when l value specified, it may not be less than m value"); 
    } 
    this.g = paramBigInteger2;
    this.p = paramBigInteger1;
    this.q = paramBigInteger3;
    this.m = paramInt1;
    this.l = paramInt2;
    this.j = paramBigInteger4;
    this.validation = paramDHValidationParameters;
  }
  
  public BigInteger getP() {
    return this.p;
  }
  
  public BigInteger getG() {
    return this.g;
  }
  
  public BigInteger getQ() {
    return this.q;
  }
  
  public BigInteger getJ() {
    return this.j;
  }
  
  public int getM() {
    return this.m;
  }
  
  public int getL() {
    return this.l;
  }
  
  public DHValidationParameters getValidationParameters() {
    return this.validation;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof DHParameters))
      return false; 
    DHParameters dHParameters = (DHParameters)paramObject;
    if (getQ() != null) {
      if (!getQ().equals(dHParameters.getQ()))
        return false; 
    } else if (dHParameters.getQ() != null) {
      return false;
    } 
    return (dHParameters.getP().equals(this.p) && dHParameters.getG().equals(this.g));
  }
  
  public int hashCode() {
    return getP().hashCode() ^ getG().hashCode() ^ ((getQ() != null) ? getQ().hashCode() : 0);
  }
}
