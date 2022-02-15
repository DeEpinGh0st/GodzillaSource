package org.bouncycastle.math.ec;

import java.math.BigInteger;

public abstract class WNafUtil {
  public static final String PRECOMP_NAME = "bc_wnaf";
  
  private static final int[] DEFAULT_WINDOW_SIZE_CUTOFFS = new int[] { 13, 41, 121, 337, 897, 2305 };
  
  private static final byte[] EMPTY_BYTES = new byte[0];
  
  private static final int[] EMPTY_INTS = new int[0];
  
  private static final ECPoint[] EMPTY_POINTS = new ECPoint[0];
  
  public static int[] generateCompactNaf(BigInteger paramBigInteger) {
    if (paramBigInteger.bitLength() >>> 16 != 0)
      throw new IllegalArgumentException("'k' must have bitlength < 2^16"); 
    if (paramBigInteger.signum() == 0)
      return EMPTY_INTS; 
    BigInteger bigInteger1 = paramBigInteger.shiftLeft(1).add(paramBigInteger);
    int i = bigInteger1.bitLength();
    int[] arrayOfInt = new int[i >> 1];
    BigInteger bigInteger2 = bigInteger1.xor(paramBigInteger);
    int j = i - 1;
    byte b1 = 0;
    byte b2 = 0;
    for (byte b3 = 1; b3 < j; b3++) {
      if (!bigInteger2.testBit(b3)) {
        b2++;
      } else {
        byte b = paramBigInteger.testBit(b3) ? -1 : 1;
        arrayOfInt[b1++] = b << 16 | b2;
        b2 = 1;
        b3++;
      } 
    } 
    arrayOfInt[b1++] = 0x10000 | b2;
    if (arrayOfInt.length > b1)
      arrayOfInt = trim(arrayOfInt, b1); 
    return arrayOfInt;
  }
  
  public static int[] generateCompactWindowNaf(int paramInt, BigInteger paramBigInteger) {
    if (paramInt == 2)
      return generateCompactNaf(paramBigInteger); 
    if (paramInt < 2 || paramInt > 16)
      throw new IllegalArgumentException("'width' must be in the range [2, 16]"); 
    if (paramBigInteger.bitLength() >>> 16 != 0)
      throw new IllegalArgumentException("'k' must have bitlength < 2^16"); 
    if (paramBigInteger.signum() == 0)
      return EMPTY_INTS; 
    int[] arrayOfInt = new int[paramBigInteger.bitLength() / paramInt + 1];
    int i = 1 << paramInt;
    int j = i - 1;
    int k = i >>> 1;
    boolean bool = false;
    byte b = 0;
    int m;
    for (m = 0; m <= paramBigInteger.bitLength(); m = paramInt) {
      if (paramBigInteger.testBit(m) == bool) {
        m++;
        continue;
      } 
      paramBigInteger = paramBigInteger.shiftRight(m);
      int n = paramBigInteger.intValue() & j;
      if (bool)
        n++; 
      bool = ((n & k) != 0);
      if (bool)
        n -= i; 
      boolean bool1 = b ? (m - 1) : m;
      arrayOfInt[b++] = n << 16 | bool1;
    } 
    if (arrayOfInt.length > b)
      arrayOfInt = trim(arrayOfInt, b); 
    return arrayOfInt;
  }
  
  public static byte[] generateJSF(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    int i = Math.max(paramBigInteger1.bitLength(), paramBigInteger2.bitLength()) + 1;
    byte[] arrayOfByte = new byte[i];
    BigInteger bigInteger1 = paramBigInteger1;
    BigInteger bigInteger2 = paramBigInteger2;
    byte b1 = 0;
    int j = 0;
    int k = 0;
    byte b2 = 0;
    while (true) {
      if ((j | k) != 0 || bigInteger1.bitLength() > b2 || bigInteger2.bitLength() > b2) {
        int m = (bigInteger1.intValue() >>> b2) + j & 0x7;
        int n = (bigInteger2.intValue() >>> b2) + k & 0x7;
        int i1 = m & 0x1;
        if (i1 != 0) {
          i1 -= m & 0x2;
          if (m + i1 == 4 && (n & 0x3) == 2)
            i1 = -i1; 
        } 
        int i2 = n & 0x1;
        if (i2 != 0) {
          i2 -= n & 0x2;
          if (n + i2 == 4 && (m & 0x3) == 2)
            i2 = -i2; 
        } 
        if (j << 1 == 1 + i1)
          j ^= 0x1; 
        if (k << 1 == 1 + i2)
          k ^= 0x1; 
        if (++b2 == 30) {
          b2 = 0;
          bigInteger1 = bigInteger1.shiftRight(30);
          bigInteger2 = bigInteger2.shiftRight(30);
        } 
        arrayOfByte[b1++] = (byte)(i1 << 4 | i2 & 0xF);
        continue;
      } 
      if (arrayOfByte.length > b1)
        arrayOfByte = trim(arrayOfByte, b1); 
      return arrayOfByte;
    } 
  }
  
  public static byte[] generateNaf(BigInteger paramBigInteger) {
    if (paramBigInteger.signum() == 0)
      return EMPTY_BYTES; 
    BigInteger bigInteger1 = paramBigInteger.shiftLeft(1).add(paramBigInteger);
    int i = bigInteger1.bitLength() - 1;
    byte[] arrayOfByte = new byte[i];
    BigInteger bigInteger2 = bigInteger1.xor(paramBigInteger);
    for (byte b = 1; b < i; b++) {
      if (bigInteger2.testBit(b)) {
        arrayOfByte[b - 1] = (byte)(paramBigInteger.testBit(b) ? -1 : 1);
        b++;
      } 
    } 
    arrayOfByte[i - 1] = 1;
    return arrayOfByte;
  }
  
  public static byte[] generateWindowNaf(int paramInt, BigInteger paramBigInteger) {
    if (paramInt == 2)
      return generateNaf(paramBigInteger); 
    if (paramInt < 2 || paramInt > 8)
      throw new IllegalArgumentException("'width' must be in the range [2, 8]"); 
    if (paramBigInteger.signum() == 0)
      return EMPTY_BYTES; 
    byte[] arrayOfByte = new byte[paramBigInteger.bitLength() + 1];
    int i = 1 << paramInt;
    int j = i - 1;
    int k = i >>> 1;
    boolean bool = false;
    int m = 0;
    int n;
    for (n = 0; n <= paramBigInteger.bitLength(); n = paramInt) {
      if (paramBigInteger.testBit(n) == bool) {
        n++;
        continue;
      } 
      paramBigInteger = paramBigInteger.shiftRight(n);
      int i1 = paramBigInteger.intValue() & j;
      if (bool)
        i1++; 
      bool = ((i1 & k) != 0);
      if (bool)
        i1 -= i; 
      m += m ? (n - 1) : n;
      arrayOfByte[m++] = (byte)i1;
    } 
    if (arrayOfByte.length > m)
      arrayOfByte = trim(arrayOfByte, m); 
    return arrayOfByte;
  }
  
  public static int getNafWeight(BigInteger paramBigInteger) {
    if (paramBigInteger.signum() == 0)
      return 0; 
    BigInteger bigInteger1 = paramBigInteger.shiftLeft(1).add(paramBigInteger);
    BigInteger bigInteger2 = bigInteger1.xor(paramBigInteger);
    return bigInteger2.bitCount();
  }
  
  public static WNafPreCompInfo getWNafPreCompInfo(ECPoint paramECPoint) {
    return getWNafPreCompInfo(paramECPoint.getCurve().getPreCompInfo(paramECPoint, "bc_wnaf"));
  }
  
  public static WNafPreCompInfo getWNafPreCompInfo(PreCompInfo paramPreCompInfo) {
    return (paramPreCompInfo != null && paramPreCompInfo instanceof WNafPreCompInfo) ? (WNafPreCompInfo)paramPreCompInfo : new WNafPreCompInfo();
  }
  
  public static int getWindowSize(int paramInt) {
    return getWindowSize(paramInt, DEFAULT_WINDOW_SIZE_CUTOFFS);
  }
  
  public static int getWindowSize(int paramInt, int[] paramArrayOfint) {
    byte b;
    for (b = 0; b < paramArrayOfint.length && paramInt >= paramArrayOfint[b]; b++);
    return b + 2;
  }
  
  public static ECPoint mapPointWithPrecomp(ECPoint paramECPoint, int paramInt, boolean paramBoolean, ECPointMap paramECPointMap) {
    ECCurve eCCurve = paramECPoint.getCurve();
    WNafPreCompInfo wNafPreCompInfo1 = precompute(paramECPoint, paramInt, paramBoolean);
    ECPoint eCPoint1 = paramECPointMap.map(paramECPoint);
    WNafPreCompInfo wNafPreCompInfo2 = getWNafPreCompInfo(eCCurve.getPreCompInfo(eCPoint1, "bc_wnaf"));
    ECPoint eCPoint2 = wNafPreCompInfo1.getTwice();
    if (eCPoint2 != null) {
      ECPoint eCPoint = paramECPointMap.map(eCPoint2);
      wNafPreCompInfo2.setTwice(eCPoint);
    } 
    ECPoint[] arrayOfECPoint1 = wNafPreCompInfo1.getPreComp();
    ECPoint[] arrayOfECPoint2 = new ECPoint[arrayOfECPoint1.length];
    for (byte b = 0; b < arrayOfECPoint1.length; b++)
      arrayOfECPoint2[b] = paramECPointMap.map(arrayOfECPoint1[b]); 
    wNafPreCompInfo2.setPreComp(arrayOfECPoint2);
    if (paramBoolean) {
      ECPoint[] arrayOfECPoint = new ECPoint[arrayOfECPoint2.length];
      for (byte b1 = 0; b1 < arrayOfECPoint.length; b1++)
        arrayOfECPoint[b1] = arrayOfECPoint2[b1].negate(); 
      wNafPreCompInfo2.setPreCompNeg(arrayOfECPoint);
    } 
    eCCurve.setPreCompInfo(eCPoint1, "bc_wnaf", wNafPreCompInfo2);
    return eCPoint1;
  }
  
  public static WNafPreCompInfo precompute(ECPoint paramECPoint, int paramInt, boolean paramBoolean) {
    ECCurve eCCurve = paramECPoint.getCurve();
    WNafPreCompInfo wNafPreCompInfo = getWNafPreCompInfo(eCCurve.getPreCompInfo(paramECPoint, "bc_wnaf"));
    int i = 0;
    int j = 1 << Math.max(0, paramInt - 2);
    ECPoint[] arrayOfECPoint = wNafPreCompInfo.getPreComp();
    if (arrayOfECPoint == null) {
      arrayOfECPoint = EMPTY_POINTS;
    } else {
      i = arrayOfECPoint.length;
    } 
    if (i < j) {
      arrayOfECPoint = resizeTable(arrayOfECPoint, j);
      if (j == 1) {
        arrayOfECPoint[0] = paramECPoint.normalize();
      } else {
        int k = i;
        if (k == 0) {
          arrayOfECPoint[0] = paramECPoint;
          k = 1;
        } 
        ECFieldElement eCFieldElement = null;
        if (j == 2) {
          arrayOfECPoint[1] = paramECPoint.threeTimes();
        } else {
          ECPoint eCPoint1 = wNafPreCompInfo.getTwice();
          ECPoint eCPoint2 = arrayOfECPoint[k - 1];
          if (eCPoint1 == null) {
            eCPoint1 = arrayOfECPoint[0].twice();
            wNafPreCompInfo.setTwice(eCPoint1);
            if (!eCPoint1.isInfinity() && ECAlgorithms.isFpCurve(eCCurve) && eCCurve.getFieldSize() >= 64) {
              ECFieldElement eCFieldElement1;
              ECFieldElement eCFieldElement2;
              switch (eCCurve.getCoordinateSystem()) {
                case 2:
                case 3:
                case 4:
                  eCFieldElement = eCPoint1.getZCoord(0);
                  eCPoint1 = eCCurve.createPoint(eCPoint1.getXCoord().toBigInteger(), eCPoint1.getYCoord().toBigInteger());
                  eCFieldElement1 = eCFieldElement.square();
                  eCFieldElement2 = eCFieldElement1.multiply(eCFieldElement);
                  eCPoint2 = eCPoint2.scaleX(eCFieldElement1).scaleY(eCFieldElement2);
                  if (i == 0)
                    arrayOfECPoint[0] = eCPoint2; 
                  break;
              } 
            } 
          } 
          while (k < j)
            arrayOfECPoint[k++] = eCPoint2 = eCPoint2.add(eCPoint1); 
        } 
        eCCurve.normalizeAll(arrayOfECPoint, i, j - i, eCFieldElement);
      } 
    } 
    wNafPreCompInfo.setPreComp(arrayOfECPoint);
    if (paramBoolean) {
      int k;
      ECPoint[] arrayOfECPoint1 = wNafPreCompInfo.getPreCompNeg();
      if (arrayOfECPoint1 == null) {
        k = 0;
        arrayOfECPoint1 = new ECPoint[j];
      } else {
        k = arrayOfECPoint1.length;
        if (k < j)
          arrayOfECPoint1 = resizeTable(arrayOfECPoint1, j); 
      } 
      while (k < j) {
        arrayOfECPoint1[k] = arrayOfECPoint[k].negate();
        k++;
      } 
      wNafPreCompInfo.setPreCompNeg(arrayOfECPoint1);
    } 
    eCCurve.setPreCompInfo(paramECPoint, "bc_wnaf", wNafPreCompInfo);
    return wNafPreCompInfo;
  }
  
  private static byte[] trim(byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  private static int[] trim(int[] paramArrayOfint, int paramInt) {
    int[] arrayOfInt = new int[paramInt];
    System.arraycopy(paramArrayOfint, 0, arrayOfInt, 0, arrayOfInt.length);
    return arrayOfInt;
  }
  
  private static ECPoint[] resizeTable(ECPoint[] paramArrayOfECPoint, int paramInt) {
    ECPoint[] arrayOfECPoint = new ECPoint[paramInt];
    System.arraycopy(paramArrayOfECPoint, 0, arrayOfECPoint, 0, paramArrayOfECPoint.length);
    return arrayOfECPoint;
  }
}
