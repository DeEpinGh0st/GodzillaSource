package org.bouncycastle.math.ec.tools;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

public class DiscoverEndomorphisms {
  private static final int radix = 16;
  
  public static void main(String[] paramArrayOfString) {
    if (paramArrayOfString.length < 1) {
      System.err.println("Expected a list of curve names as arguments");
      return;
    } 
    for (byte b = 0; b < paramArrayOfString.length; b++)
      discoverEndomorphisms(paramArrayOfString[b]); 
  }
  
  public static void discoverEndomorphisms(X9ECParameters paramX9ECParameters) {
    if (paramX9ECParameters == null)
      throw new NullPointerException("x9"); 
    ECCurve eCCurve = paramX9ECParameters.getCurve();
    if (ECAlgorithms.isFpCurve(eCCurve)) {
      BigInteger bigInteger = eCCurve.getField().getCharacteristic();
      if (eCCurve.getA().isZero() && bigInteger.mod(ECConstants.THREE).equals(ECConstants.ONE)) {
        System.out.println("Curve has a 'GLV Type B' endomorphism with these parameters:");
        printGLVTypeBParameters(paramX9ECParameters);
      } 
    } 
  }
  
  private static void discoverEndomorphisms(String paramString) {
    X9ECParameters x9ECParameters = ECNamedCurveTable.getByName(paramString);
    if (x9ECParameters == null) {
      System.err.println("Unknown curve: " + paramString);
      return;
    } 
    ECCurve eCCurve = x9ECParameters.getCurve();
    if (ECAlgorithms.isFpCurve(eCCurve)) {
      BigInteger bigInteger = eCCurve.getField().getCharacteristic();
      if (eCCurve.getA().isZero() && bigInteger.mod(ECConstants.THREE).equals(ECConstants.ONE)) {
        System.out.println("Curve '" + paramString + "' has a 'GLV Type B' endomorphism with these parameters:");
        printGLVTypeBParameters(x9ECParameters);
      } 
    } 
  }
  
  private static void printGLVTypeBParameters(X9ECParameters paramX9ECParameters) {
    BigInteger[] arrayOfBigInteger = solveQuadraticEquation(paramX9ECParameters.getN(), ECConstants.ONE, ECConstants.ONE);
    ECFieldElement[] arrayOfECFieldElement = findBetaValues(paramX9ECParameters.getCurve());
    printGLVTypeBParameters(paramX9ECParameters, arrayOfBigInteger[0], arrayOfECFieldElement);
    System.out.println("OR");
    printGLVTypeBParameters(paramX9ECParameters, arrayOfBigInteger[1], arrayOfECFieldElement);
  }
  
  private static void printGLVTypeBParameters(X9ECParameters paramX9ECParameters, BigInteger paramBigInteger, ECFieldElement[] paramArrayOfECFieldElement) {
    ECPoint eCPoint1 = paramX9ECParameters.getG().normalize();
    ECPoint eCPoint2 = eCPoint1.multiply(paramBigInteger).normalize();
    if (!eCPoint1.getYCoord().equals(eCPoint2.getYCoord()))
      throw new IllegalStateException("Derivation of GLV Type B parameters failed unexpectedly"); 
    ECFieldElement eCFieldElement = paramArrayOfECFieldElement[0];
    if (!eCPoint1.getXCoord().multiply(eCFieldElement).equals(eCPoint2.getXCoord())) {
      eCFieldElement = paramArrayOfECFieldElement[1];
      if (!eCPoint1.getXCoord().multiply(eCFieldElement).equals(eCPoint2.getXCoord()))
        throw new IllegalStateException("Derivation of GLV Type B parameters failed unexpectedly"); 
    } 
    BigInteger bigInteger1 = paramX9ECParameters.getN();
    BigInteger[] arrayOfBigInteger1 = null;
    BigInteger[] arrayOfBigInteger2 = null;
    BigInteger[] arrayOfBigInteger3 = extEuclidGLV(bigInteger1, paramBigInteger);
    arrayOfBigInteger1 = new BigInteger[] { arrayOfBigInteger3[2], arrayOfBigInteger3[3].negate() };
    arrayOfBigInteger2 = chooseShortest(new BigInteger[] { arrayOfBigInteger3[0], arrayOfBigInteger3[1].negate() }, new BigInteger[] { arrayOfBigInteger3[4], arrayOfBigInteger3[5].negate() });
    if (!isVectorBoundedBySqrt(arrayOfBigInteger2, bigInteger1) && areRelativelyPrime(arrayOfBigInteger1[0], arrayOfBigInteger1[1])) {
      BigInteger bigInteger5 = arrayOfBigInteger1[0];
      BigInteger bigInteger6 = arrayOfBigInteger1[1];
      BigInteger bigInteger7 = bigInteger5.add(bigInteger6.multiply(paramBigInteger)).divide(bigInteger1);
      BigInteger[] arrayOfBigInteger = extEuclidBezout(new BigInteger[] { bigInteger7.abs(), bigInteger6.abs() });
      if (arrayOfBigInteger != null) {
        BigInteger bigInteger8 = arrayOfBigInteger[0];
        BigInteger bigInteger9 = arrayOfBigInteger[1];
        if (bigInteger7.signum() < 0)
          bigInteger8 = bigInteger8.negate(); 
        if (bigInteger6.signum() > 0)
          bigInteger9 = bigInteger9.negate(); 
        BigInteger bigInteger10 = bigInteger7.multiply(bigInteger8).subtract(bigInteger6.multiply(bigInteger9));
        if (!bigInteger10.equals(ECConstants.ONE))
          throw new IllegalStateException(); 
        BigInteger bigInteger11 = bigInteger9.multiply(bigInteger1).subtract(bigInteger8.multiply(paramBigInteger));
        BigInteger bigInteger12 = bigInteger8.negate();
        BigInteger bigInteger13 = bigInteger11.negate();
        BigInteger bigInteger14 = isqrt(bigInteger1.subtract(ECConstants.ONE)).add(ECConstants.ONE);
        BigInteger[] arrayOfBigInteger4 = calculateRange(bigInteger12, bigInteger14, bigInteger6);
        BigInteger[] arrayOfBigInteger5 = calculateRange(bigInteger13, bigInteger14, bigInteger5);
        BigInteger[] arrayOfBigInteger6 = intersect(arrayOfBigInteger4, arrayOfBigInteger5);
        if (arrayOfBigInteger6 != null)
          for (BigInteger bigInteger = arrayOfBigInteger6[0]; bigInteger.compareTo(arrayOfBigInteger6[1]) <= 0; bigInteger = bigInteger.add(ECConstants.ONE)) {
            BigInteger[] arrayOfBigInteger7 = { bigInteger11.add(bigInteger.multiply(bigInteger5)), bigInteger8.add(bigInteger.multiply(bigInteger6)) };
            if (isShorter(arrayOfBigInteger7, arrayOfBigInteger2))
              arrayOfBigInteger2 = arrayOfBigInteger7; 
          }  
      } 
    } 
    BigInteger bigInteger2 = arrayOfBigInteger1[0].multiply(arrayOfBigInteger2[1]).subtract(arrayOfBigInteger1[1].multiply(arrayOfBigInteger2[0]));
    int i = bigInteger1.bitLength() + 16 - (bigInteger1.bitLength() & 0x7);
    BigInteger bigInteger3 = roundQuotient(arrayOfBigInteger2[1].shiftLeft(i), bigInteger2);
    BigInteger bigInteger4 = roundQuotient(arrayOfBigInteger1[1].shiftLeft(i), bigInteger2).negate();
    printProperty("Beta", eCFieldElement.toBigInteger().toString(16));
    printProperty("Lambda", paramBigInteger.toString(16));
    printProperty("v1", "{ " + arrayOfBigInteger1[0].toString(16) + ", " + arrayOfBigInteger1[1].toString(16) + " }");
    printProperty("v2", "{ " + arrayOfBigInteger2[0].toString(16) + ", " + arrayOfBigInteger2[1].toString(16) + " }");
    printProperty("d", bigInteger2.toString(16));
    printProperty("(OPT) g1", bigInteger3.toString(16));
    printProperty("(OPT) g2", bigInteger4.toString(16));
    printProperty("(OPT) bits", Integer.toString(i));
  }
  
  private static void printProperty(String paramString, Object paramObject) {
    StringBuffer stringBuffer = new StringBuffer("  ");
    stringBuffer.append(paramString);
    while (stringBuffer.length() < 20)
      stringBuffer.append(' '); 
    stringBuffer.append("= ");
    stringBuffer.append(paramObject.toString());
    System.out.println(stringBuffer.toString());
  }
  
  private static boolean areRelativelyPrime(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    return paramBigInteger1.gcd(paramBigInteger2).equals(ECConstants.ONE);
  }
  
  private static BigInteger[] calculateRange(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    BigInteger bigInteger1 = paramBigInteger1.subtract(paramBigInteger2).divide(paramBigInteger3);
    BigInteger bigInteger2 = paramBigInteger1.add(paramBigInteger2).divide(paramBigInteger3);
    return order(bigInteger1, bigInteger2);
  }
  
  private static BigInteger[] extEuclidBezout(BigInteger[] paramArrayOfBigInteger) {
    boolean bool = (paramArrayOfBigInteger[0].compareTo(paramArrayOfBigInteger[1]) < 0) ? true : false;
    if (bool)
      swap(paramArrayOfBigInteger); 
    BigInteger bigInteger1 = paramArrayOfBigInteger[0];
    BigInteger bigInteger2 = paramArrayOfBigInteger[1];
    BigInteger bigInteger3 = ECConstants.ONE;
    BigInteger bigInteger4 = ECConstants.ZERO;
    BigInteger bigInteger5 = ECConstants.ZERO;
    BigInteger bigInteger6;
    for (bigInteger6 = ECConstants.ONE; bigInteger2.compareTo(ECConstants.ONE) > 0; bigInteger6 = bigInteger10) {
      BigInteger[] arrayOfBigInteger1 = bigInteger1.divideAndRemainder(bigInteger2);
      BigInteger bigInteger7 = arrayOfBigInteger1[0];
      BigInteger bigInteger8 = arrayOfBigInteger1[1];
      BigInteger bigInteger9 = bigInteger3.subtract(bigInteger7.multiply(bigInteger4));
      BigInteger bigInteger10 = bigInteger5.subtract(bigInteger7.multiply(bigInteger6));
      bigInteger1 = bigInteger2;
      bigInteger2 = bigInteger8;
      bigInteger3 = bigInteger4;
      bigInteger4 = bigInteger9;
      bigInteger5 = bigInteger6;
    } 
    if (bigInteger2.signum() <= 0)
      return null; 
    BigInteger[] arrayOfBigInteger = { bigInteger4, bigInteger6 };
    if (bool)
      swap(arrayOfBigInteger); 
    return arrayOfBigInteger;
  }
  
  private static BigInteger[] extEuclidGLV(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    BigInteger bigInteger1 = paramBigInteger1;
    BigInteger bigInteger2 = paramBigInteger2;
    BigInteger bigInteger3 = ECConstants.ZERO;
    for (BigInteger bigInteger4 = ECConstants.ONE;; bigInteger4 = bigInteger7) {
      BigInteger[] arrayOfBigInteger = bigInteger1.divideAndRemainder(bigInteger2);
      BigInteger bigInteger5 = arrayOfBigInteger[0];
      BigInteger bigInteger6 = arrayOfBigInteger[1];
      BigInteger bigInteger7 = bigInteger3.subtract(bigInteger5.multiply(bigInteger4));
      if (isLessThanSqrt(bigInteger2, paramBigInteger1))
        return new BigInteger[] { bigInteger1, bigInteger3, bigInteger2, bigInteger4, bigInteger6, bigInteger7 }; 
      bigInteger1 = bigInteger2;
      bigInteger2 = bigInteger6;
      bigInteger3 = bigInteger4;
    } 
  }
  
  private static BigInteger[] chooseShortest(BigInteger[] paramArrayOfBigInteger1, BigInteger[] paramArrayOfBigInteger2) {
    return isShorter(paramArrayOfBigInteger1, paramArrayOfBigInteger2) ? paramArrayOfBigInteger1 : paramArrayOfBigInteger2;
  }
  
  private static BigInteger[] intersect(BigInteger[] paramArrayOfBigInteger1, BigInteger[] paramArrayOfBigInteger2) {
    BigInteger bigInteger1 = paramArrayOfBigInteger1[0].max(paramArrayOfBigInteger2[0]);
    BigInteger bigInteger2 = paramArrayOfBigInteger1[1].min(paramArrayOfBigInteger2[1]);
    return (bigInteger1.compareTo(bigInteger2) > 0) ? null : new BigInteger[] { bigInteger1, bigInteger2 };
  }
  
  private static boolean isLessThanSqrt(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    paramBigInteger1 = paramBigInteger1.abs();
    paramBigInteger2 = paramBigInteger2.abs();
    int i = paramBigInteger2.bitLength();
    int j = paramBigInteger1.bitLength() * 2;
    int k = j - 1;
    return (k <= i && (j < i || paramBigInteger1.multiply(paramBigInteger1).compareTo(paramBigInteger2) < 0));
  }
  
  private static boolean isShorter(BigInteger[] paramArrayOfBigInteger1, BigInteger[] paramArrayOfBigInteger2) {
    BigInteger bigInteger1 = paramArrayOfBigInteger1[0].abs();
    BigInteger bigInteger2 = paramArrayOfBigInteger1[1].abs();
    BigInteger bigInteger3 = paramArrayOfBigInteger2[0].abs();
    BigInteger bigInteger4 = paramArrayOfBigInteger2[1].abs();
    boolean bool1 = (bigInteger1.compareTo(bigInteger3) < 0) ? true : false;
    boolean bool2 = (bigInteger2.compareTo(bigInteger4) < 0) ? true : false;
    if (bool1 == bool2)
      return bool1; 
    BigInteger bigInteger5 = bigInteger1.multiply(bigInteger1).add(bigInteger2.multiply(bigInteger2));
    BigInteger bigInteger6 = bigInteger3.multiply(bigInteger3).add(bigInteger4.multiply(bigInteger4));
    return (bigInteger5.compareTo(bigInteger6) < 0);
  }
  
  private static boolean isVectorBoundedBySqrt(BigInteger[] paramArrayOfBigInteger, BigInteger paramBigInteger) {
    BigInteger bigInteger = paramArrayOfBigInteger[0].abs().max(paramArrayOfBigInteger[1].abs());
    return isLessThanSqrt(bigInteger, paramBigInteger);
  }
  
  private static BigInteger[] order(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    return (paramBigInteger1.compareTo(paramBigInteger2) <= 0) ? new BigInteger[] { paramBigInteger1, paramBigInteger2 } : new BigInteger[] { paramBigInteger2, paramBigInteger1 };
  }
  
  private static BigInteger roundQuotient(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    boolean bool = (paramBigInteger1.signum() != paramBigInteger2.signum()) ? true : false;
    paramBigInteger1 = paramBigInteger1.abs();
    paramBigInteger2 = paramBigInteger2.abs();
    BigInteger bigInteger = paramBigInteger1.add(paramBigInteger2.shiftRight(1)).divide(paramBigInteger2);
    return bool ? bigInteger.negate() : bigInteger;
  }
  
  private static BigInteger[] solveQuadraticEquation(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    BigInteger bigInteger1 = paramBigInteger2.multiply(paramBigInteger2).subtract(paramBigInteger3.shiftLeft(2)).mod(paramBigInteger1);
    BigInteger bigInteger2 = (new ECFieldElement.Fp(paramBigInteger1, bigInteger1)).sqrt().toBigInteger();
    BigInteger bigInteger3 = paramBigInteger1.subtract(bigInteger2);
    if (bigInteger2.testBit(0)) {
      bigInteger3 = bigInteger3.add(paramBigInteger1);
    } else {
      bigInteger2 = bigInteger2.add(paramBigInteger1);
    } 
    return new BigInteger[] { bigInteger2.shiftRight(1), bigInteger3.shiftRight(1) };
  }
  
  private static ECFieldElement[] findBetaValues(ECCurve paramECCurve) {
    BigInteger bigInteger1 = paramECCurve.getField().getCharacteristic();
    BigInteger bigInteger2 = bigInteger1.divide(ECConstants.THREE);
    SecureRandom secureRandom = new SecureRandom();
    while (true) {
      BigInteger bigInteger4 = BigIntegers.createRandomInRange(ECConstants.TWO, bigInteger1.subtract(ECConstants.TWO), secureRandom);
      BigInteger bigInteger3 = bigInteger4.modPow(bigInteger2, bigInteger1);
      if (!bigInteger3.equals(ECConstants.ONE)) {
        ECFieldElement eCFieldElement = paramECCurve.fromBigInteger(bigInteger3);
        return new ECFieldElement[] { eCFieldElement, eCFieldElement.square() };
      } 
    } 
  }
  
  private static BigInteger isqrt(BigInteger paramBigInteger) {
    for (BigInteger bigInteger = paramBigInteger.shiftRight(paramBigInteger.bitLength() / 2);; bigInteger = bigInteger1) {
      BigInteger bigInteger1 = bigInteger.add(paramBigInteger.divide(bigInteger)).shiftRight(1);
      if (bigInteger1.equals(bigInteger))
        return bigInteger1; 
    } 
  }
  
  private static void swap(BigInteger[] paramArrayOfBigInteger) {
    BigInteger bigInteger = paramArrayOfBigInteger[0];
    paramArrayOfBigInteger[0] = paramArrayOfBigInteger[1];
    paramArrayOfBigInteger[1] = bigInteger;
  }
}
