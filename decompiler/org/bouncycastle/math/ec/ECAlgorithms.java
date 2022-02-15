package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.endo.ECEndomorphism;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;
import org.bouncycastle.math.field.FiniteField;

public class ECAlgorithms {
  public static boolean isF2mCurve(ECCurve paramECCurve) {
    return isF2mField(paramECCurve.getField());
  }
  
  public static boolean isF2mField(FiniteField paramFiniteField) {
    return (paramFiniteField.getDimension() > 1 && paramFiniteField.getCharacteristic().equals(ECConstants.TWO) && paramFiniteField instanceof org.bouncycastle.math.field.PolynomialExtensionField);
  }
  
  public static boolean isFpCurve(ECCurve paramECCurve) {
    return isFpField(paramECCurve.getField());
  }
  
  public static boolean isFpField(FiniteField paramFiniteField) {
    return (paramFiniteField.getDimension() == 1);
  }
  
  public static ECPoint sumOfMultiplies(ECPoint[] paramArrayOfECPoint, BigInteger[] paramArrayOfBigInteger) {
    if (paramArrayOfECPoint == null || paramArrayOfBigInteger == null || paramArrayOfECPoint.length != paramArrayOfBigInteger.length || paramArrayOfECPoint.length < 1)
      throw new IllegalArgumentException("point and scalar arrays should be non-null, and of equal, non-zero, length"); 
    int i = paramArrayOfECPoint.length;
    switch (i) {
      case 1:
        return paramArrayOfECPoint[0].multiply(paramArrayOfBigInteger[0]);
      case 2:
        return sumOfTwoMultiplies(paramArrayOfECPoint[0], paramArrayOfBigInteger[0], paramArrayOfECPoint[1], paramArrayOfBigInteger[1]);
    } 
    ECPoint eCPoint = paramArrayOfECPoint[0];
    ECCurve eCCurve = eCPoint.getCurve();
    ECPoint[] arrayOfECPoint = new ECPoint[i];
    arrayOfECPoint[0] = eCPoint;
    for (byte b = 1; b < i; b++)
      arrayOfECPoint[b] = importPoint(eCCurve, paramArrayOfECPoint[b]); 
    ECEndomorphism eCEndomorphism = eCCurve.getEndomorphism();
    return (eCEndomorphism instanceof GLVEndomorphism) ? validatePoint(implSumOfMultipliesGLV(arrayOfECPoint, paramArrayOfBigInteger, (GLVEndomorphism)eCEndomorphism)) : validatePoint(implSumOfMultiplies(arrayOfECPoint, paramArrayOfBigInteger));
  }
  
  public static ECPoint sumOfTwoMultiplies(ECPoint paramECPoint1, BigInteger paramBigInteger1, ECPoint paramECPoint2, BigInteger paramBigInteger2) {
    ECCurve eCCurve = paramECPoint1.getCurve();
    paramECPoint2 = importPoint(eCCurve, paramECPoint2);
    if (eCCurve instanceof ECCurve.AbstractF2m) {
      ECCurve.AbstractF2m abstractF2m = (ECCurve.AbstractF2m)eCCurve;
      if (abstractF2m.isKoblitz())
        return validatePoint(paramECPoint1.multiply(paramBigInteger1).add(paramECPoint2.multiply(paramBigInteger2))); 
    } 
    ECEndomorphism eCEndomorphism = eCCurve.getEndomorphism();
    return (eCEndomorphism instanceof GLVEndomorphism) ? validatePoint(implSumOfMultipliesGLV(new ECPoint[] { paramECPoint1, paramECPoint2 }, new BigInteger[] { paramBigInteger1, paramBigInteger2 }, (GLVEndomorphism)eCEndomorphism)) : validatePoint(implShamirsTrickWNaf(paramECPoint1, paramBigInteger1, paramECPoint2, paramBigInteger2));
  }
  
  public static ECPoint shamirsTrick(ECPoint paramECPoint1, BigInteger paramBigInteger1, ECPoint paramECPoint2, BigInteger paramBigInteger2) {
    ECCurve eCCurve = paramECPoint1.getCurve();
    paramECPoint2 = importPoint(eCCurve, paramECPoint2);
    return validatePoint(implShamirsTrickJsf(paramECPoint1, paramBigInteger1, paramECPoint2, paramBigInteger2));
  }
  
  public static ECPoint importPoint(ECCurve paramECCurve, ECPoint paramECPoint) {
    ECCurve eCCurve = paramECPoint.getCurve();
    if (!paramECCurve.equals(eCCurve))
      throw new IllegalArgumentException("Point must be on the same curve"); 
    return paramECCurve.importPoint(paramECPoint);
  }
  
  public static void montgomeryTrick(ECFieldElement[] paramArrayOfECFieldElement, int paramInt1, int paramInt2) {
    montgomeryTrick(paramArrayOfECFieldElement, paramInt1, paramInt2, null);
  }
  
  public static void montgomeryTrick(ECFieldElement[] paramArrayOfECFieldElement, int paramInt1, int paramInt2, ECFieldElement paramECFieldElement) {
    ECFieldElement[] arrayOfECFieldElement = new ECFieldElement[paramInt2];
    arrayOfECFieldElement[0] = paramArrayOfECFieldElement[paramInt1];
    byte b = 0;
    while (++b < paramInt2)
      arrayOfECFieldElement[b] = arrayOfECFieldElement[b - 1].multiply(paramArrayOfECFieldElement[paramInt1 + b]); 
    b--;
    if (paramECFieldElement != null)
      arrayOfECFieldElement[b] = arrayOfECFieldElement[b].multiply(paramECFieldElement); 
    ECFieldElement eCFieldElement;
    for (eCFieldElement = arrayOfECFieldElement[b].invert(); b > 0; eCFieldElement = eCFieldElement.multiply(eCFieldElement1)) {
      int i = paramInt1 + b--;
      ECFieldElement eCFieldElement1 = paramArrayOfECFieldElement[i];
      paramArrayOfECFieldElement[i] = arrayOfECFieldElement[b].multiply(eCFieldElement);
    } 
    paramArrayOfECFieldElement[paramInt1] = eCFieldElement;
  }
  
  public static ECPoint referenceMultiply(ECPoint paramECPoint, BigInteger paramBigInteger) {
    BigInteger bigInteger = paramBigInteger.abs();
    ECPoint eCPoint = paramECPoint.getCurve().getInfinity();
    int i = bigInteger.bitLength();
    if (i > 0) {
      if (bigInteger.testBit(0))
        eCPoint = paramECPoint; 
      for (byte b = 1; b < i; b++) {
        paramECPoint = paramECPoint.twice();
        if (bigInteger.testBit(b))
          eCPoint = eCPoint.add(paramECPoint); 
      } 
    } 
    return (paramBigInteger.signum() < 0) ? eCPoint.negate() : eCPoint;
  }
  
  public static ECPoint validatePoint(ECPoint paramECPoint) {
    if (!paramECPoint.isValid())
      throw new IllegalArgumentException("Invalid point"); 
    return paramECPoint;
  }
  
  static ECPoint implShamirsTrickJsf(ECPoint paramECPoint1, BigInteger paramBigInteger1, ECPoint paramECPoint2, BigInteger paramBigInteger2) {
    ECCurve eCCurve = paramECPoint1.getCurve();
    ECPoint eCPoint1 = eCCurve.getInfinity();
    ECPoint eCPoint2 = paramECPoint1.add(paramECPoint2);
    ECPoint eCPoint3 = paramECPoint1.subtract(paramECPoint2);
    ECPoint[] arrayOfECPoint1 = { paramECPoint2, eCPoint3, paramECPoint1, eCPoint2 };
    eCCurve.normalizeAll(arrayOfECPoint1);
    ECPoint[] arrayOfECPoint2 = { arrayOfECPoint1[3].negate(), arrayOfECPoint1[2].negate(), arrayOfECPoint1[1].negate(), arrayOfECPoint1[0].negate(), eCPoint1, arrayOfECPoint1[0], arrayOfECPoint1[1], arrayOfECPoint1[2], arrayOfECPoint1[3] };
    byte[] arrayOfByte = WNafUtil.generateJSF(paramBigInteger1, paramBigInteger2);
    ECPoint eCPoint4 = eCPoint1;
    int i = arrayOfByte.length;
    while (--i >= 0) {
      byte b = arrayOfByte[i];
      int j = b << 24 >> 28;
      int k = b << 28 >> 28;
      int m = 4 + j * 3 + k;
      eCPoint4 = eCPoint4.twicePlus(arrayOfECPoint2[m]);
    } 
    return eCPoint4;
  }
  
  static ECPoint implShamirsTrickWNaf(ECPoint paramECPoint1, BigInteger paramBigInteger1, ECPoint paramECPoint2, BigInteger paramBigInteger2) {
    boolean bool1 = (paramBigInteger1.signum() < 0) ? true : false;
    boolean bool2 = (paramBigInteger2.signum() < 0) ? true : false;
    paramBigInteger1 = paramBigInteger1.abs();
    paramBigInteger2 = paramBigInteger2.abs();
    int i = Math.max(2, Math.min(16, WNafUtil.getWindowSize(paramBigInteger1.bitLength())));
    int j = Math.max(2, Math.min(16, WNafUtil.getWindowSize(paramBigInteger2.bitLength())));
    WNafPreCompInfo wNafPreCompInfo1 = WNafUtil.precompute(paramECPoint1, i, true);
    WNafPreCompInfo wNafPreCompInfo2 = WNafUtil.precompute(paramECPoint2, j, true);
    ECPoint[] arrayOfECPoint1 = bool1 ? wNafPreCompInfo1.getPreCompNeg() : wNafPreCompInfo1.getPreComp();
    ECPoint[] arrayOfECPoint2 = bool2 ? wNafPreCompInfo2.getPreCompNeg() : wNafPreCompInfo2.getPreComp();
    ECPoint[] arrayOfECPoint3 = bool1 ? wNafPreCompInfo1.getPreComp() : wNafPreCompInfo1.getPreCompNeg();
    ECPoint[] arrayOfECPoint4 = bool2 ? wNafPreCompInfo2.getPreComp() : wNafPreCompInfo2.getPreCompNeg();
    byte[] arrayOfByte1 = WNafUtil.generateWindowNaf(i, paramBigInteger1);
    byte[] arrayOfByte2 = WNafUtil.generateWindowNaf(j, paramBigInteger2);
    return implShamirsTrickWNaf(arrayOfECPoint1, arrayOfECPoint3, arrayOfByte1, arrayOfECPoint2, arrayOfECPoint4, arrayOfByte2);
  }
  
  static ECPoint implShamirsTrickWNaf(ECPoint paramECPoint, BigInteger paramBigInteger1, ECPointMap paramECPointMap, BigInteger paramBigInteger2) {
    boolean bool1 = (paramBigInteger1.signum() < 0) ? true : false;
    boolean bool2 = (paramBigInteger2.signum() < 0) ? true : false;
    paramBigInteger1 = paramBigInteger1.abs();
    paramBigInteger2 = paramBigInteger2.abs();
    int i = Math.max(2, Math.min(16, WNafUtil.getWindowSize(Math.max(paramBigInteger1.bitLength(), paramBigInteger2.bitLength()))));
    ECPoint eCPoint = WNafUtil.mapPointWithPrecomp(paramECPoint, i, true, paramECPointMap);
    WNafPreCompInfo wNafPreCompInfo1 = WNafUtil.getWNafPreCompInfo(paramECPoint);
    WNafPreCompInfo wNafPreCompInfo2 = WNafUtil.getWNafPreCompInfo(eCPoint);
    ECPoint[] arrayOfECPoint1 = bool1 ? wNafPreCompInfo1.getPreCompNeg() : wNafPreCompInfo1.getPreComp();
    ECPoint[] arrayOfECPoint2 = bool2 ? wNafPreCompInfo2.getPreCompNeg() : wNafPreCompInfo2.getPreComp();
    ECPoint[] arrayOfECPoint3 = bool1 ? wNafPreCompInfo1.getPreComp() : wNafPreCompInfo1.getPreCompNeg();
    ECPoint[] arrayOfECPoint4 = bool2 ? wNafPreCompInfo2.getPreComp() : wNafPreCompInfo2.getPreCompNeg();
    byte[] arrayOfByte1 = WNafUtil.generateWindowNaf(i, paramBigInteger1);
    byte[] arrayOfByte2 = WNafUtil.generateWindowNaf(i, paramBigInteger2);
    return implShamirsTrickWNaf(arrayOfECPoint1, arrayOfECPoint3, arrayOfByte1, arrayOfECPoint2, arrayOfECPoint4, arrayOfByte2);
  }
  
  private static ECPoint implShamirsTrickWNaf(ECPoint[] paramArrayOfECPoint1, ECPoint[] paramArrayOfECPoint2, byte[] paramArrayOfbyte1, ECPoint[] paramArrayOfECPoint3, ECPoint[] paramArrayOfECPoint4, byte[] paramArrayOfbyte2) {
    int i = Math.max(paramArrayOfbyte1.length, paramArrayOfbyte2.length);
    ECCurve eCCurve = paramArrayOfECPoint1[0].getCurve();
    ECPoint eCPoint1 = eCCurve.getInfinity();
    ECPoint eCPoint2 = eCPoint1;
    byte b = 0;
    for (int j = i - 1; j >= 0; j--) {
      boolean bool1 = (j < paramArrayOfbyte1.length) ? paramArrayOfbyte1[j] : false;
      boolean bool2 = (j < paramArrayOfbyte2.length) ? paramArrayOfbyte2[j] : false;
      if ((bool1 | bool2) == 0) {
        b++;
      } else {
        ECPoint eCPoint = eCPoint1;
        if (bool1) {
          int k = Math.abs(bool1);
          ECPoint[] arrayOfECPoint = bool1 ? paramArrayOfECPoint2 : paramArrayOfECPoint1;
          eCPoint = eCPoint.add(arrayOfECPoint[k >>> 1]);
        } 
        if (bool2) {
          int k = Math.abs(bool2);
          ECPoint[] arrayOfECPoint = bool2 ? paramArrayOfECPoint4 : paramArrayOfECPoint3;
          eCPoint = eCPoint.add(arrayOfECPoint[k >>> 1]);
        } 
        if (b > 0) {
          eCPoint2 = eCPoint2.timesPow2(b);
          b = 0;
        } 
        eCPoint2 = eCPoint2.twicePlus(eCPoint);
      } 
    } 
    if (b > 0)
      eCPoint2 = eCPoint2.timesPow2(b); 
    return eCPoint2;
  }
  
  static ECPoint implSumOfMultiplies(ECPoint[] paramArrayOfECPoint, BigInteger[] paramArrayOfBigInteger) {
    int i = paramArrayOfECPoint.length;
    boolean[] arrayOfBoolean = new boolean[i];
    WNafPreCompInfo[] arrayOfWNafPreCompInfo = new WNafPreCompInfo[i];
    byte[][] arrayOfByte = new byte[i][];
    for (byte b = 0; b < i; b++) {
      BigInteger bigInteger = paramArrayOfBigInteger[b];
      arrayOfBoolean[b] = (bigInteger.signum() < 0);
      bigInteger = bigInteger.abs();
      int j = Math.max(2, Math.min(16, WNafUtil.getWindowSize(bigInteger.bitLength())));
      arrayOfWNafPreCompInfo[b] = WNafUtil.precompute(paramArrayOfECPoint[b], j, true);
      arrayOfByte[b] = WNafUtil.generateWindowNaf(j, bigInteger);
    } 
    return implSumOfMultiplies(arrayOfBoolean, arrayOfWNafPreCompInfo, arrayOfByte);
  }
  
  static ECPoint implSumOfMultipliesGLV(ECPoint[] paramArrayOfECPoint, BigInteger[] paramArrayOfBigInteger, GLVEndomorphism paramGLVEndomorphism) {
    BigInteger bigInteger = paramArrayOfECPoint[0].getCurve().getOrder();
    int i = paramArrayOfECPoint.length;
    BigInteger[] arrayOfBigInteger = new BigInteger[i << 1];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < i) {
      BigInteger[] arrayOfBigInteger1 = paramGLVEndomorphism.decomposeScalar(paramArrayOfBigInteger[b1].mod(bigInteger));
      arrayOfBigInteger[b2++] = arrayOfBigInteger1[0];
      arrayOfBigInteger[b2++] = arrayOfBigInteger1[1];
      b1++;
    } 
    ECPointMap eCPointMap = paramGLVEndomorphism.getPointMap();
    if (paramGLVEndomorphism.hasEfficientPointMap())
      return implSumOfMultiplies(paramArrayOfECPoint, eCPointMap, arrayOfBigInteger); 
    ECPoint[] arrayOfECPoint = new ECPoint[i << 1];
    byte b3 = 0;
    byte b4 = 0;
    while (b3 < i) {
      ECPoint eCPoint1 = paramArrayOfECPoint[b3];
      ECPoint eCPoint2 = eCPointMap.map(eCPoint1);
      arrayOfECPoint[b4++] = eCPoint1;
      arrayOfECPoint[b4++] = eCPoint2;
      b3++;
    } 
    return implSumOfMultiplies(arrayOfECPoint, arrayOfBigInteger);
  }
  
  static ECPoint implSumOfMultiplies(ECPoint[] paramArrayOfECPoint, ECPointMap paramECPointMap, BigInteger[] paramArrayOfBigInteger) {
    int i = paramArrayOfECPoint.length;
    int j = i << 1;
    boolean[] arrayOfBoolean = new boolean[j];
    WNafPreCompInfo[] arrayOfWNafPreCompInfo = new WNafPreCompInfo[j];
    byte[][] arrayOfByte = new byte[j][];
    for (byte b = 0; b < i; b++) {
      int k = b << 1;
      int m = k + 1;
      BigInteger bigInteger1 = paramArrayOfBigInteger[k];
      arrayOfBoolean[k] = (bigInteger1.signum() < 0);
      bigInteger1 = bigInteger1.abs();
      BigInteger bigInteger2 = paramArrayOfBigInteger[m];
      arrayOfBoolean[m] = (bigInteger2.signum() < 0);
      bigInteger2 = bigInteger2.abs();
      int n = Math.max(2, Math.min(16, WNafUtil.getWindowSize(Math.max(bigInteger1.bitLength(), bigInteger2.bitLength()))));
      ECPoint eCPoint1 = paramArrayOfECPoint[b];
      ECPoint eCPoint2 = WNafUtil.mapPointWithPrecomp(eCPoint1, n, true, paramECPointMap);
      arrayOfWNafPreCompInfo[k] = WNafUtil.getWNafPreCompInfo(eCPoint1);
      arrayOfWNafPreCompInfo[m] = WNafUtil.getWNafPreCompInfo(eCPoint2);
      arrayOfByte[k] = WNafUtil.generateWindowNaf(n, bigInteger1);
      arrayOfByte[m] = WNafUtil.generateWindowNaf(n, bigInteger2);
    } 
    return implSumOfMultiplies(arrayOfBoolean, arrayOfWNafPreCompInfo, arrayOfByte);
  }
  
  private static ECPoint implSumOfMultiplies(boolean[] paramArrayOfboolean, WNafPreCompInfo[] paramArrayOfWNafPreCompInfo, byte[][] paramArrayOfbyte) {
    int i = 0;
    int j = paramArrayOfbyte.length;
    for (byte b1 = 0; b1 < j; b1++)
      i = Math.max(i, (paramArrayOfbyte[b1]).length); 
    ECCurve eCCurve = paramArrayOfWNafPreCompInfo[0].getPreComp()[0].getCurve();
    ECPoint eCPoint1 = eCCurve.getInfinity();
    ECPoint eCPoint2 = eCPoint1;
    byte b2 = 0;
    for (int k = i - 1; k >= 0; k--) {
      ECPoint eCPoint = eCPoint1;
      for (byte b = 0; b < j; b++) {
        byte[] arrayOfByte = paramArrayOfbyte[b];
        boolean bool = (k < arrayOfByte.length) ? arrayOfByte[k] : false;
        if (bool) {
          int m = Math.abs(bool);
          WNafPreCompInfo wNafPreCompInfo = paramArrayOfWNafPreCompInfo[b];
          ECPoint[] arrayOfECPoint = ((bool) == paramArrayOfboolean[b]) ? wNafPreCompInfo.getPreComp() : wNafPreCompInfo.getPreCompNeg();
          eCPoint = eCPoint.add(arrayOfECPoint[m >>> 1]);
        } 
      } 
      if (eCPoint == eCPoint1) {
        b2++;
      } else {
        if (b2 > 0) {
          eCPoint2 = eCPoint2.timesPow2(b2);
          b2 = 0;
        } 
        eCPoint2 = eCPoint2.twicePlus(eCPoint);
      } 
    } 
    if (b2 > 0)
      eCPoint2 = eCPoint2.timesPow2(b2); 
    return eCPoint2;
  }
}
