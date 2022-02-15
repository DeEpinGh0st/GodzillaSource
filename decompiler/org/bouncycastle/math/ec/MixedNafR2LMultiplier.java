package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class MixedNafR2LMultiplier extends AbstractECMultiplier {
  protected int additionCoord;
  
  protected int doublingCoord;
  
  public MixedNafR2LMultiplier() {
    this(2, 4);
  }
  
  public MixedNafR2LMultiplier(int paramInt1, int paramInt2) {
    this.additionCoord = paramInt1;
    this.doublingCoord = paramInt2;
  }
  
  protected ECPoint multiplyPositive(ECPoint paramECPoint, BigInteger paramBigInteger) {
    ECCurve eCCurve1 = paramECPoint.getCurve();
    ECCurve eCCurve2 = configureCurve(eCCurve1, this.additionCoord);
    ECCurve eCCurve3 = configureCurve(eCCurve1, this.doublingCoord);
    int[] arrayOfInt = WNafUtil.generateCompactNaf(paramBigInteger);
    ECPoint eCPoint1 = eCCurve2.getInfinity();
    ECPoint eCPoint2 = eCCurve3.importPoint(paramECPoint);
    int i = 0;
    for (byte b = 0; b < arrayOfInt.length; b++) {
      int j = arrayOfInt[b];
      int k = j >> 16;
      i += j & 0xFFFF;
      eCPoint2 = eCPoint2.timesPow2(i);
      ECPoint eCPoint = eCCurve2.importPoint(eCPoint2);
      if (k < 0)
        eCPoint = eCPoint.negate(); 
      eCPoint1 = eCPoint1.add(eCPoint);
      i = 1;
    } 
    return eCCurve1.importPoint(eCPoint1);
  }
  
  protected ECCurve configureCurve(ECCurve paramECCurve, int paramInt) {
    if (paramECCurve.getCoordinateSystem() == paramInt)
      return paramECCurve; 
    if (!paramECCurve.supportsCoordinateSystem(paramInt))
      throw new IllegalArgumentException("Coordinate system " + paramInt + " not supported by this curve"); 
    return paramECCurve.configure().setCoordinateSystem(paramInt).create();
  }
}
