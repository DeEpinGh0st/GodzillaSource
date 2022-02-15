package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;

public class GLVMultiplier extends AbstractECMultiplier {
  protected final ECCurve curve;
  
  protected final GLVEndomorphism glvEndomorphism;
  
  public GLVMultiplier(ECCurve paramECCurve, GLVEndomorphism paramGLVEndomorphism) {
    if (paramECCurve == null || paramECCurve.getOrder() == null)
      throw new IllegalArgumentException("Need curve with known group order"); 
    this.curve = paramECCurve;
    this.glvEndomorphism = paramGLVEndomorphism;
  }
  
  protected ECPoint multiplyPositive(ECPoint paramECPoint, BigInteger paramBigInteger) {
    if (!this.curve.equals(paramECPoint.getCurve()))
      throw new IllegalStateException(); 
    BigInteger bigInteger1 = paramECPoint.getCurve().getOrder();
    BigInteger[] arrayOfBigInteger = this.glvEndomorphism.decomposeScalar(paramBigInteger.mod(bigInteger1));
    BigInteger bigInteger2 = arrayOfBigInteger[0];
    BigInteger bigInteger3 = arrayOfBigInteger[1];
    ECPointMap eCPointMap = this.glvEndomorphism.getPointMap();
    return this.glvEndomorphism.hasEfficientPointMap() ? ECAlgorithms.implShamirsTrickWNaf(paramECPoint, bigInteger2, eCPointMap, bigInteger3) : ECAlgorithms.implShamirsTrickWNaf(paramECPoint, bigInteger2, eCPointMap.map(paramECPoint), bigInteger3);
  }
}
