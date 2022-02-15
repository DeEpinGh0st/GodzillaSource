package org.bouncycastle.crypto.agreement.srp;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.BigIntegers;

public class SRP6Util {
  private static BigInteger ZERO = BigInteger.valueOf(0L);
  
  private static BigInteger ONE = BigInteger.valueOf(1L);
  
  public static BigInteger calculateK(Digest paramDigest, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    return hashPaddedPair(paramDigest, paramBigInteger1, paramBigInteger1, paramBigInteger2);
  }
  
  public static BigInteger calculateU(Digest paramDigest, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    return hashPaddedPair(paramDigest, paramBigInteger1, paramBigInteger2, paramBigInteger3);
  }
  
  public static BigInteger calculateX(Digest paramDigest, BigInteger paramBigInteger, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    byte[] arrayOfByte = new byte[paramDigest.getDigestSize()];
    paramDigest.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
    paramDigest.update((byte)58);
    paramDigest.update(paramArrayOfbyte3, 0, paramArrayOfbyte3.length);
    paramDigest.doFinal(arrayOfByte, 0);
    paramDigest.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    paramDigest.update(arrayOfByte, 0, arrayOfByte.length);
    paramDigest.doFinal(arrayOfByte, 0);
    return new BigInteger(1, arrayOfByte);
  }
  
  public static BigInteger generatePrivateValue(Digest paramDigest, BigInteger paramBigInteger1, BigInteger paramBigInteger2, SecureRandom paramSecureRandom) {
    int i = Math.min(256, paramBigInteger1.bitLength() / 2);
    BigInteger bigInteger1 = ONE.shiftLeft(i - 1);
    BigInteger bigInteger2 = paramBigInteger1.subtract(ONE);
    return BigIntegers.createRandomInRange(bigInteger1, bigInteger2, paramSecureRandom);
  }
  
  public static BigInteger validatePublicValue(BigInteger paramBigInteger1, BigInteger paramBigInteger2) throws CryptoException {
    paramBigInteger2 = paramBigInteger2.mod(paramBigInteger1);
    if (paramBigInteger2.equals(ZERO))
      throw new CryptoException("Invalid public value: 0"); 
    return paramBigInteger2;
  }
  
  public static BigInteger calculateM1(Digest paramDigest, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) {
    return hashPaddedTriplet(paramDigest, paramBigInteger1, paramBigInteger2, paramBigInteger3, paramBigInteger4);
  }
  
  public static BigInteger calculateM2(Digest paramDigest, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) {
    return hashPaddedTriplet(paramDigest, paramBigInteger1, paramBigInteger2, paramBigInteger3, paramBigInteger4);
  }
  
  public static BigInteger calculateKey(Digest paramDigest, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    int i = (paramBigInteger1.bitLength() + 7) / 8;
    byte[] arrayOfByte1 = getPadded(paramBigInteger2, i);
    paramDigest.update(arrayOfByte1, 0, arrayOfByte1.length);
    byte[] arrayOfByte2 = new byte[paramDigest.getDigestSize()];
    paramDigest.doFinal(arrayOfByte2, 0);
    return new BigInteger(1, arrayOfByte2);
  }
  
  private static BigInteger hashPaddedTriplet(Digest paramDigest, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) {
    int i = (paramBigInteger1.bitLength() + 7) / 8;
    byte[] arrayOfByte1 = getPadded(paramBigInteger2, i);
    byte[] arrayOfByte2 = getPadded(paramBigInteger3, i);
    byte[] arrayOfByte3 = getPadded(paramBigInteger4, i);
    paramDigest.update(arrayOfByte1, 0, arrayOfByte1.length);
    paramDigest.update(arrayOfByte2, 0, arrayOfByte2.length);
    paramDigest.update(arrayOfByte3, 0, arrayOfByte3.length);
    byte[] arrayOfByte4 = new byte[paramDigest.getDigestSize()];
    paramDigest.doFinal(arrayOfByte4, 0);
    return new BigInteger(1, arrayOfByte4);
  }
  
  private static BigInteger hashPaddedPair(Digest paramDigest, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    int i = (paramBigInteger1.bitLength() + 7) / 8;
    byte[] arrayOfByte1 = getPadded(paramBigInteger2, i);
    byte[] arrayOfByte2 = getPadded(paramBigInteger3, i);
    paramDigest.update(arrayOfByte1, 0, arrayOfByte1.length);
    paramDigest.update(arrayOfByte2, 0, arrayOfByte2.length);
    byte[] arrayOfByte3 = new byte[paramDigest.getDigestSize()];
    paramDigest.doFinal(arrayOfByte3, 0);
    return new BigInteger(1, arrayOfByte3);
  }
  
  private static byte[] getPadded(BigInteger paramBigInteger, int paramInt) {
    byte[] arrayOfByte = BigIntegers.asUnsignedByteArray(paramBigInteger);
    if (arrayOfByte.length < paramInt) {
      byte[] arrayOfByte1 = new byte[paramInt];
      System.arraycopy(arrayOfByte, 0, arrayOfByte1, paramInt - arrayOfByte.length, arrayOfByte.length);
      arrayOfByte = arrayOfByte1;
    } 
    return arrayOfByte;
  }
}
