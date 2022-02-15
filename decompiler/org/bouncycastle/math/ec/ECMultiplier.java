package org.bouncycastle.math.ec;

import java.math.BigInteger;

public interface ECMultiplier {
  ECPoint multiply(ECPoint paramECPoint, BigInteger paramBigInteger);
}
