package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class WNafL2RMultiplier extends AbstractECMultiplier {
  protected ECPoint multiplyPositive(ECPoint paramECPoint, BigInteger paramBigInteger) {
    int i = Math.max(2, Math.min(16, getWindowSize(paramBigInteger.bitLength())));
    WNafPreCompInfo wNafPreCompInfo = WNafUtil.precompute(paramECPoint, i, true);
    ECPoint[] arrayOfECPoint1 = wNafPreCompInfo.getPreComp();
    ECPoint[] arrayOfECPoint2 = wNafPreCompInfo.getPreCompNeg();
    int[] arrayOfInt = WNafUtil.generateCompactWindowNaf(i, paramBigInteger);
    ECPoint eCPoint = paramECPoint.getCurve().getInfinity();
    int j = arrayOfInt.length;
    if (j > 1) {
      int k = arrayOfInt[--j];
      int m = k >> 16;
      int n = k & 0xFFFF;
      int i1 = Math.abs(m);
      ECPoint[] arrayOfECPoint = (m < 0) ? arrayOfECPoint2 : arrayOfECPoint1;
      if (i1 << 2 < 1 << i) {
        byte b = LongArray.bitLengths[i1];
        int i2 = i - b;
        int i3 = i1 ^ 1 << b - 1;
        int i4 = (1 << i - 1) - 1;
        int i5 = (i3 << i2) + 1;
        eCPoint = arrayOfECPoint[i4 >>> 1].add(arrayOfECPoint[i5 >>> 1]);
        n -= i2;
      } else {
        eCPoint = arrayOfECPoint[i1 >>> 1];
      } 
      eCPoint = eCPoint.timesPow2(n);
    } 
    while (j > 0) {
      int k = arrayOfInt[--j];
      int m = k >> 16;
      int n = k & 0xFFFF;
      int i1 = Math.abs(m);
      ECPoint[] arrayOfECPoint = (m < 0) ? arrayOfECPoint2 : arrayOfECPoint1;
      ECPoint eCPoint1 = arrayOfECPoint[i1 >>> 1];
      eCPoint = eCPoint.twicePlus(eCPoint1);
      eCPoint = eCPoint.timesPow2(n);
    } 
    return eCPoint;
  }
  
  protected int getWindowSize(int paramInt) {
    return WNafUtil.getWindowSize(paramInt);
  }
}
