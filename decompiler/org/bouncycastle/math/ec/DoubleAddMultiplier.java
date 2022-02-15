package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class DoubleAddMultiplier extends AbstractECMultiplier {
  protected ECPoint multiplyPositive(ECPoint paramECPoint, BigInteger paramBigInteger) {
    ECPoint[] arrayOfECPoint = { paramECPoint.getCurve().getInfinity(), paramECPoint };
    int i = paramBigInteger.bitLength();
    for (byte b = 0; b < i; b++) {
      byte b1 = paramBigInteger.testBit(b) ? 1 : 0;
      int j = 1 - b1;
      arrayOfECPoint[j] = arrayOfECPoint[j].twicePlus(arrayOfECPoint[b1]);
    } 
    return arrayOfECPoint[0];
  }
}
