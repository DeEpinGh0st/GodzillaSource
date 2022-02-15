package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;

public class JPAKEUtil {
  static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  static final BigInteger ONE = BigInteger.valueOf(1L);
  
  public static BigInteger generateX1(BigInteger paramBigInteger, SecureRandom paramSecureRandom) {
    BigInteger bigInteger1 = ZERO;
    BigInteger bigInteger2 = paramBigInteger.subtract(ONE);
    return BigIntegers.createRandomInRange(bigInteger1, bigInteger2, paramSecureRandom);
  }
  
  public static BigInteger generateX2(BigInteger paramBigInteger, SecureRandom paramSecureRandom) {
    BigInteger bigInteger1 = ONE;
    BigInteger bigInteger2 = paramBigInteger.subtract(ONE);
    return BigIntegers.createRandomInRange(bigInteger1, bigInteger2, paramSecureRandom);
  }
  
  public static BigInteger calculateS(char[] paramArrayOfchar) {
    return new BigInteger(Strings.toUTF8ByteArray(paramArrayOfchar));
  }
  
  public static BigInteger calculateGx(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    return paramBigInteger2.modPow(paramBigInteger3, paramBigInteger1);
  }
  
  public static BigInteger calculateGA(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) {
    return paramBigInteger2.multiply(paramBigInteger3).multiply(paramBigInteger4).mod(paramBigInteger1);
  }
  
  public static BigInteger calculateX2s(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    return paramBigInteger2.multiply(paramBigInteger3).mod(paramBigInteger1);
  }
  
  public static BigInteger calculateA(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) {
    return paramBigInteger3.modPow(paramBigInteger4, paramBigInteger1);
  }
  
  public static BigInteger[] calculateZeroKnowledgeProof(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, String paramString, Digest paramDigest, SecureRandom paramSecureRandom) {
    BigInteger[] arrayOfBigInteger = new BigInteger[2];
    BigInteger bigInteger1 = ZERO;
    BigInteger bigInteger2 = paramBigInteger2.subtract(ONE);
    BigInteger bigInteger3 = BigIntegers.createRandomInRange(bigInteger1, bigInteger2, paramSecureRandom);
    BigInteger bigInteger4 = paramBigInteger3.modPow(bigInteger3, paramBigInteger1);
    BigInteger bigInteger5 = calculateHashForZeroKnowledgeProof(paramBigInteger3, bigInteger4, paramBigInteger4, paramString, paramDigest);
    arrayOfBigInteger[0] = bigInteger4;
    arrayOfBigInteger[1] = bigInteger3.subtract(paramBigInteger5.multiply(bigInteger5)).mod(paramBigInteger2);
    return arrayOfBigInteger;
  }
  
  private static BigInteger calculateHashForZeroKnowledgeProof(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, String paramString, Digest paramDigest) {
    paramDigest.reset();
    updateDigestIncludingSize(paramDigest, paramBigInteger1);
    updateDigestIncludingSize(paramDigest, paramBigInteger2);
    updateDigestIncludingSize(paramDigest, paramBigInteger3);
    updateDigestIncludingSize(paramDigest, paramString);
    byte[] arrayOfByte = new byte[paramDigest.getDigestSize()];
    paramDigest.doFinal(arrayOfByte, 0);
    return new BigInteger(arrayOfByte);
  }
  
  public static void validateGx4(BigInteger paramBigInteger) throws CryptoException {
    if (paramBigInteger.equals(ONE))
      throw new CryptoException("g^x validation failed.  g^x should not be 1."); 
  }
  
  public static void validateGa(BigInteger paramBigInteger) throws CryptoException {
    if (paramBigInteger.equals(ONE))
      throw new CryptoException("ga is equal to 1.  It should not be.  The chances of this happening are on the order of 2^160 for a 160-bit q.  Try again."); 
  }
  
  public static void validateZeroKnowledgeProof(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger[] paramArrayOfBigInteger, String paramString, Digest paramDigest) throws CryptoException {
    BigInteger bigInteger1 = paramArrayOfBigInteger[0];
    BigInteger bigInteger2 = paramArrayOfBigInteger[1];
    BigInteger bigInteger3 = calculateHashForZeroKnowledgeProof(paramBigInteger3, bigInteger1, paramBigInteger4, paramString, paramDigest);
    if (paramBigInteger4.compareTo(ZERO) != 1 || paramBigInteger4.compareTo(paramBigInteger1) != -1 || paramBigInteger4.modPow(paramBigInteger2, paramBigInteger1).compareTo(ONE) != 0 || paramBigInteger3.modPow(bigInteger2, paramBigInteger1).multiply(paramBigInteger4.modPow(bigInteger3, paramBigInteger1)).mod(paramBigInteger1).compareTo(bigInteger1) != 0)
      throw new CryptoException("Zero-knowledge proof validation failed"); 
  }
  
  public static BigInteger calculateKeyingMaterial(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6) {
    return paramBigInteger3.modPow(paramBigInteger4.multiply(paramBigInteger5).negate().mod(paramBigInteger2), paramBigInteger1).multiply(paramBigInteger6).modPow(paramBigInteger4, paramBigInteger1);
  }
  
  public static void validateParticipantIdsDiffer(String paramString1, String paramString2) throws CryptoException {
    if (paramString1.equals(paramString2))
      throw new CryptoException("Both participants are using the same participantId (" + paramString1 + "). This is not allowed. " + "Each participant must use a unique participantId."); 
  }
  
  public static void validateParticipantIdsEqual(String paramString1, String paramString2) throws CryptoException {
    if (!paramString1.equals(paramString2))
      throw new CryptoException("Received payload from incorrect partner (" + paramString2 + "). Expected to receive payload from " + paramString1 + "."); 
  }
  
  public static void validateNotNull(Object paramObject, String paramString) {
    if (paramObject == null)
      throw new NullPointerException(paramString + " must not be null"); 
  }
  
  public static BigInteger calculateMacTag(String paramString1, String paramString2, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, Digest paramDigest) {
    byte[] arrayOfByte1 = calculateMacKey(paramBigInteger5, paramDigest);
    HMac hMac = new HMac(paramDigest);
    byte[] arrayOfByte2 = new byte[hMac.getMacSize()];
    hMac.init((CipherParameters)new KeyParameter(arrayOfByte1));
    updateMac((Mac)hMac, "KC_1_U");
    updateMac((Mac)hMac, paramString1);
    updateMac((Mac)hMac, paramString2);
    updateMac((Mac)hMac, paramBigInteger1);
    updateMac((Mac)hMac, paramBigInteger2);
    updateMac((Mac)hMac, paramBigInteger3);
    updateMac((Mac)hMac, paramBigInteger4);
    hMac.doFinal(arrayOfByte2, 0);
    Arrays.fill(arrayOfByte1, (byte)0);
    return new BigInteger(arrayOfByte2);
  }
  
  private static byte[] calculateMacKey(BigInteger paramBigInteger, Digest paramDigest) {
    paramDigest.reset();
    updateDigest(paramDigest, paramBigInteger);
    updateDigest(paramDigest, "JPAKE_KC");
    byte[] arrayOfByte = new byte[paramDigest.getDigestSize()];
    paramDigest.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static void validateMacTag(String paramString1, String paramString2, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, Digest paramDigest, BigInteger paramBigInteger6) throws CryptoException {
    BigInteger bigInteger = calculateMacTag(paramString2, paramString1, paramBigInteger3, paramBigInteger4, paramBigInteger1, paramBigInteger2, paramBigInteger5, paramDigest);
    if (!bigInteger.equals(paramBigInteger6))
      throw new CryptoException("Partner MacTag validation failed. Therefore, the password, MAC, or digest algorithm of each participant does not match."); 
  }
  
  private static void updateDigest(Digest paramDigest, BigInteger paramBigInteger) {
    byte[] arrayOfByte = BigIntegers.asUnsignedByteArray(paramBigInteger);
    paramDigest.update(arrayOfByte, 0, arrayOfByte.length);
    Arrays.fill(arrayOfByte, (byte)0);
  }
  
  private static void updateDigestIncludingSize(Digest paramDigest, BigInteger paramBigInteger) {
    byte[] arrayOfByte = BigIntegers.asUnsignedByteArray(paramBigInteger);
    paramDigest.update(intToByteArray(arrayOfByte.length), 0, 4);
    paramDigest.update(arrayOfByte, 0, arrayOfByte.length);
    Arrays.fill(arrayOfByte, (byte)0);
  }
  
  private static void updateDigest(Digest paramDigest, String paramString) {
    byte[] arrayOfByte = Strings.toUTF8ByteArray(paramString);
    paramDigest.update(arrayOfByte, 0, arrayOfByte.length);
    Arrays.fill(arrayOfByte, (byte)0);
  }
  
  private static void updateDigestIncludingSize(Digest paramDigest, String paramString) {
    byte[] arrayOfByte = Strings.toUTF8ByteArray(paramString);
    paramDigest.update(intToByteArray(arrayOfByte.length), 0, 4);
    paramDigest.update(arrayOfByte, 0, arrayOfByte.length);
    Arrays.fill(arrayOfByte, (byte)0);
  }
  
  private static void updateMac(Mac paramMac, BigInteger paramBigInteger) {
    byte[] arrayOfByte = BigIntegers.asUnsignedByteArray(paramBigInteger);
    paramMac.update(arrayOfByte, 0, arrayOfByte.length);
    Arrays.fill(arrayOfByte, (byte)0);
  }
  
  private static void updateMac(Mac paramMac, String paramString) {
    byte[] arrayOfByte = Strings.toUTF8ByteArray(paramString);
    paramMac.update(arrayOfByte, 0, arrayOfByte.length);
    Arrays.fill(arrayOfByte, (byte)0);
  }
  
  private static byte[] intToByteArray(int paramInt) {
    return new byte[] { (byte)(paramInt >>> 24), (byte)(paramInt >>> 16), (byte)(paramInt >>> 8), (byte)paramInt };
  }
}
