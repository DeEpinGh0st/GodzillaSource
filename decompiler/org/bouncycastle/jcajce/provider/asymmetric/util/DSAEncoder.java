package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.io.IOException;
import java.math.BigInteger;

public interface DSAEncoder {
  byte[] encode(BigInteger paramBigInteger1, BigInteger paramBigInteger2) throws IOException;
  
  BigInteger[] decode(byte[] paramArrayOfbyte) throws IOException;
}
