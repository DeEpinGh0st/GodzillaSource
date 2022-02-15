package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

public class DSAParameters implements CipherParameters {
  private BigInteger g;
  
  private BigInteger q;
  
  private BigInteger p;
  
  private DSAValidationParameters validation;
  
  public DSAParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    this.g = paramBigInteger3;
    this.p = paramBigInteger1;
    this.q = paramBigInteger2;
  }
  
  public DSAParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, DSAValidationParameters paramDSAValidationParameters) {
    this.g = paramBigInteger3;
    this.p = paramBigInteger1;
    this.q = paramBigInteger2;
    this.validation = paramDSAValidationParameters;
  }
  
  public BigInteger getP() {
    return this.p;
  }
  
  public BigInteger getQ() {
    return this.q;
  }
  
  public BigInteger getG() {
    return this.g;
  }
  
  public DSAValidationParameters getValidationParameters() {
    return this.validation;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof DSAParameters))
      return false; 
    DSAParameters dSAParameters = (DSAParameters)paramObject;
    return (dSAParameters.getP().equals(this.p) && dSAParameters.getQ().equals(this.q) && dSAParameters.getG().equals(this.g));
  }
  
  public int hashCode() {
    return getP().hashCode() ^ getQ().hashCode() ^ getG().hashCode();
  }
}
