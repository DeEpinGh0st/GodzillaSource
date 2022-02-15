package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class ECParameterSpec implements AlgorithmParameterSpec {
  private ECCurve curve;
  
  private byte[] seed;
  
  private ECPoint G;
  
  private BigInteger n;
  
  private BigInteger h;
  
  public ECParameterSpec(ECCurve paramECCurve, ECPoint paramECPoint, BigInteger paramBigInteger) {
    this.curve = paramECCurve;
    this.G = paramECPoint.normalize();
    this.n = paramBigInteger;
    this.h = BigInteger.valueOf(1L);
    this.seed = null;
  }
  
  public ECParameterSpec(ECCurve paramECCurve, ECPoint paramECPoint, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this.curve = paramECCurve;
    this.G = paramECPoint.normalize();
    this.n = paramBigInteger1;
    this.h = paramBigInteger2;
    this.seed = null;
  }
  
  public ECParameterSpec(ECCurve paramECCurve, ECPoint paramECPoint, BigInteger paramBigInteger1, BigInteger paramBigInteger2, byte[] paramArrayOfbyte) {
    this.curve = paramECCurve;
    this.G = paramECPoint.normalize();
    this.n = paramBigInteger1;
    this.h = paramBigInteger2;
    this.seed = paramArrayOfbyte;
  }
  
  public ECCurve getCurve() {
    return this.curve;
  }
  
  public ECPoint getG() {
    return this.G;
  }
  
  public BigInteger getN() {
    return this.n;
  }
  
  public BigInteger getH() {
    return this.h;
  }
  
  public byte[] getSeed() {
    return this.seed;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof ECParameterSpec))
      return false; 
    ECParameterSpec eCParameterSpec = (ECParameterSpec)paramObject;
    return (getCurve().equals(eCParameterSpec.getCurve()) && getG().equals(eCParameterSpec.getG()));
  }
  
  public int hashCode() {
    return getCurve().hashCode() ^ getG().hashCode();
  }
}
