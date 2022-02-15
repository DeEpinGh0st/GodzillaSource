package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class MontgomeryLadderMultiplier extends AbstractECMultiplier {
  protected ECPoint multiplyPositive(ECPoint paramECPoint, BigInteger paramBigInteger) {
    ECPoint[] arrayOfECPoint = { paramECPoint.getCurve().getInfinity(), paramECPoint };
    int i = paramBigInteger.bitLength();
    int j = i;
    while (--j >= 0) {
      byte b = paramBigInteger.testBit(j) ? 1 : 0;
      int k = 1 - b;
      arrayOfECPoint[k] = arrayOfECPoint[k].add(arrayOfECPoint[b]);
      arrayOfECPoint[b] = arrayOfECPoint[b].twice();
    } 
    return arrayOfECPoint[0];
  }
}
