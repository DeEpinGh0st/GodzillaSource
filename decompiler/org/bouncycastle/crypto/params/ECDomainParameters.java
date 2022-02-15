package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class ECDomainParameters implements ECConstants {
  private ECCurve curve;
  
  private byte[] seed;
  
  private ECPoint G;
  
  private BigInteger n;
  
  private BigInteger h;
  
  public ECDomainParameters(ECCurve paramECCurve, ECPoint paramECPoint, BigInteger paramBigInteger) {
    this(paramECCurve, paramECPoint, paramBigInteger, ONE, null);
  }
  
  public ECDomainParameters(ECCurve paramECCurve, ECPoint paramECPoint, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this(paramECCurve, paramECPoint, paramBigInteger1, paramBigInteger2, null);
  }
  
  public ECDomainParameters(ECCurve paramECCurve, ECPoint paramECPoint, BigInteger paramBigInteger1, BigInteger paramBigInteger2, byte[] paramArrayOfbyte) {
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
    return Arrays.clone(this.seed);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof ECDomainParameters) {
      ECDomainParameters eCDomainParameters = (ECDomainParameters)paramObject;
      return (this.curve.equals(eCDomainParameters.curve) && this.G.equals(eCDomainParameters.G) && this.n.equals(eCDomainParameters.n) && this.h.equals(eCDomainParameters.h));
    } 
    return false;
  }
  
  public int hashCode() {
    int i = this.curve.hashCode();
    i *= 37;
    i ^= this.G.hashCode();
    i *= 37;
    i ^= this.n.hashCode();
    i *= 37;
    i ^= this.h.hashCode();
    return i;
  }
}
