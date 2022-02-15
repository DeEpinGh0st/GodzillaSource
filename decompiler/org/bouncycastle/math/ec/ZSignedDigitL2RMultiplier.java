package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class ZSignedDigitL2RMultiplier extends AbstractECMultiplier {
  protected ECPoint multiplyPositive(ECPoint paramECPoint, BigInteger paramBigInteger) {
    ECPoint eCPoint1 = paramECPoint.normalize();
    ECPoint eCPoint2 = eCPoint1.negate();
    null = eCPoint1;
    int i = paramBigInteger.bitLength();
    int j = paramBigInteger.getLowestSetBit();
    int k = i;
    while (--k > j)
      null = null.twicePlus(paramBigInteger.testBit(k) ? eCPoint1 : eCPoint2); 
    return null.timesPow2(j);
  }
}
