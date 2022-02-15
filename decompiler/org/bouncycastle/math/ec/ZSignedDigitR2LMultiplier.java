package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class ZSignedDigitR2LMultiplier extends AbstractECMultiplier {
  protected ECPoint multiplyPositive(ECPoint paramECPoint, BigInteger paramBigInteger) {
    null = paramECPoint.getCurve().getInfinity();
    ECPoint eCPoint = paramECPoint;
    int i = paramBigInteger.bitLength();
    int j = paramBigInteger.getLowestSetBit();
    eCPoint = eCPoint.timesPow2(j);
    int k = j;
    while (++k < i) {
      null = null.add(paramBigInteger.testBit(k) ? eCPoint : eCPoint.negate());
      eCPoint = eCPoint.twice();
    } 
    return null.add(eCPoint);
  }
}
