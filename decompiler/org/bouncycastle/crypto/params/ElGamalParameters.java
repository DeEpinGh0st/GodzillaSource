package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

public class ElGamalParameters implements CipherParameters {
  private BigInteger g;
  
  private BigInteger p;
  
  private int l;
  
  public ElGamalParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this(paramBigInteger1, paramBigInteger2, 0);
  }
  
  public ElGamalParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt) {
    this.g = paramBigInteger2;
    this.p = paramBigInteger1;
    this.l = paramInt;
  }
  
  public BigInteger getP() {
    return this.p;
  }
  
  public BigInteger getG() {
    return this.g;
  }
  
  public int getL() {
    return this.l;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof ElGamalParameters))
      return false; 
    ElGamalParameters elGamalParameters = (ElGamalParameters)paramObject;
    return (elGamalParameters.getP().equals(this.p) && elGamalParameters.getG().equals(this.g) && elGamalParameters.getL() == this.l);
  }
  
  public int hashCode() {
    return (getP().hashCode() ^ getG().hashCode()) + this.l;
  }
}
