package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class FixedPointCombMultiplier extends AbstractECMultiplier {
  protected ECPoint multiplyPositive(ECPoint paramECPoint, BigInteger paramBigInteger) {
    ECCurve eCCurve = paramECPoint.getCurve();
    int i = FixedPointUtil.getCombSize(eCCurve);
    if (paramBigInteger.bitLength() > i)
      throw new IllegalStateException("fixed-point comb doesn't support scalars larger than the curve order"); 
    int j = getWidthForCombSize(i);
    FixedPointPreCompInfo fixedPointPreCompInfo = FixedPointUtil.precompute(paramECPoint, j);
    ECPoint[] arrayOfECPoint = fixedPointPreCompInfo.getPreComp();
    int k = fixedPointPreCompInfo.getWidth();
    int m = (i + k - 1) / k;
    ECPoint eCPoint = eCCurve.getInfinity();
    int n = m * k - 1;
    for (byte b = 0; b < m; b++) {
      int i1 = 0;
      int i2;
      for (i2 = n - b; i2 >= 0; i2 -= m) {
        i1 <<= 1;
        if (paramBigInteger.testBit(i2))
          i1 |= 0x1; 
      } 
      eCPoint = eCPoint.twicePlus(arrayOfECPoint[i1]);
    } 
    return eCPoint.add(fixedPointPreCompInfo.getOffset());
  }
  
  protected int getWidthForCombSize(int paramInt) {
    return (paramInt > 257) ? 6 : 5;
  }
}
