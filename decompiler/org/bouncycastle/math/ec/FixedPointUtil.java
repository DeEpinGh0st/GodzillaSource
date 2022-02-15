package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class FixedPointUtil {
  public static final String PRECOMP_NAME = "bc_fixed_point";
  
  public static int getCombSize(ECCurve paramECCurve) {
    BigInteger bigInteger = paramECCurve.getOrder();
    return (bigInteger == null) ? (paramECCurve.getFieldSize() + 1) : bigInteger.bitLength();
  }
  
  public static FixedPointPreCompInfo getFixedPointPreCompInfo(PreCompInfo paramPreCompInfo) {
    return (paramPreCompInfo != null && paramPreCompInfo instanceof FixedPointPreCompInfo) ? (FixedPointPreCompInfo)paramPreCompInfo : new FixedPointPreCompInfo();
  }
  
  public static FixedPointPreCompInfo precompute(ECPoint paramECPoint, int paramInt) {
    ECCurve eCCurve = paramECPoint.getCurve();
    int i = 1 << paramInt;
    FixedPointPreCompInfo fixedPointPreCompInfo = getFixedPointPreCompInfo(eCCurve.getPreCompInfo(paramECPoint, "bc_fixed_point"));
    ECPoint[] arrayOfECPoint = fixedPointPreCompInfo.getPreComp();
    if (arrayOfECPoint == null || arrayOfECPoint.length < i) {
      int j = getCombSize(eCCurve);
      int k = (j + paramInt - 1) / paramInt;
      ECPoint[] arrayOfECPoint1 = new ECPoint[paramInt + 1];
      arrayOfECPoint1[0] = paramECPoint;
      int m;
      for (m = 1; m < paramInt; m++)
        arrayOfECPoint1[m] = arrayOfECPoint1[m - 1].timesPow2(k); 
      arrayOfECPoint1[paramInt] = arrayOfECPoint1[0].subtract(arrayOfECPoint1[1]);
      eCCurve.normalizeAll(arrayOfECPoint1);
      arrayOfECPoint = new ECPoint[i];
      arrayOfECPoint[0] = arrayOfECPoint1[0];
      for (m = paramInt - 1; m >= 0; m--) {
        ECPoint eCPoint = arrayOfECPoint1[m];
        int n = 1 << m;
        int i1;
        for (i1 = n; i1 < i; i1 += n << 1)
          arrayOfECPoint[i1] = arrayOfECPoint[i1 - n].add(eCPoint); 
      } 
      eCCurve.normalizeAll(arrayOfECPoint);
      fixedPointPreCompInfo.setOffset(arrayOfECPoint1[paramInt]);
      fixedPointPreCompInfo.setPreComp(arrayOfECPoint);
      fixedPointPreCompInfo.setWidth(paramInt);
      eCCurve.setPreCompInfo(paramECPoint, "bc_fixed_point", fixedPointPreCompInfo);
    } 
    return fixedPointPreCompInfo;
  }
}
