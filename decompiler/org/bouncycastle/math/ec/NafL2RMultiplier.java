package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class NafL2RMultiplier extends AbstractECMultiplier {
  protected ECPoint multiplyPositive(ECPoint paramECPoint, BigInteger paramBigInteger) {
    int[] arrayOfInt = WNafUtil.generateCompactNaf(paramBigInteger);
    ECPoint eCPoint1 = paramECPoint.normalize();
    ECPoint eCPoint2 = eCPoint1.negate();
    ECPoint eCPoint3 = paramECPoint.getCurve().getInfinity();
    int i = arrayOfInt.length;
    while (--i >= 0) {
      int j = arrayOfInt[i];
      int k = j >> 16;
      int m = j & 0xFFFF;
      eCPoint3 = eCPoint3.twicePlus((k < 0) ? eCPoint2 : eCPoint1);
      eCPoint3 = eCPoint3.timesPow2(m);
    } 
    return eCPoint3;
  }
}
