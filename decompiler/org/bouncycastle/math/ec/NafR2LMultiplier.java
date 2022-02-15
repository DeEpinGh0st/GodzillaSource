package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class NafR2LMultiplier extends AbstractECMultiplier {
  protected ECPoint multiplyPositive(ECPoint paramECPoint, BigInteger paramBigInteger) {
    int[] arrayOfInt = WNafUtil.generateCompactNaf(paramBigInteger);
    ECPoint eCPoint1 = paramECPoint.getCurve().getInfinity();
    ECPoint eCPoint2 = paramECPoint;
    int i = 0;
    for (byte b = 0; b < arrayOfInt.length; b++) {
      int j = arrayOfInt[b];
      int k = j >> 16;
      i += j & 0xFFFF;
      eCPoint2 = eCPoint2.timesPow2(i);
      eCPoint1 = eCPoint1.add((k < 0) ? eCPoint2.negate() : eCPoint2);
      i = 1;
    } 
    return eCPoint1;
  }
}
