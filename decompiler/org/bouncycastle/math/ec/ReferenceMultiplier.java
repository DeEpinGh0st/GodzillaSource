package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class ReferenceMultiplier extends AbstractECMultiplier {
  protected ECPoint multiplyPositive(ECPoint paramECPoint, BigInteger paramBigInteger) {
    return ECAlgorithms.referenceMultiply(paramECPoint, paramBigInteger);
  }
}
