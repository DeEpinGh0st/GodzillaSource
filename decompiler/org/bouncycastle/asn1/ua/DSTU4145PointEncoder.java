package org.bouncycastle.asn1.ua;

import java.math.BigInteger;
import java.util.Random;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

public abstract class DSTU4145PointEncoder {
  private static ECFieldElement trace(ECFieldElement paramECFieldElement) {
    ECFieldElement eCFieldElement = paramECFieldElement;
    for (byte b = 1; b < paramECFieldElement.getFieldSize(); b++)
      eCFieldElement = eCFieldElement.square().add(paramECFieldElement); 
    return eCFieldElement;
  }
  
  private static ECFieldElement solveQuadraticEquation(ECCurve paramECCurve, ECFieldElement paramECFieldElement) {
    if (paramECFieldElement.isZero())
      return paramECFieldElement; 
    ECFieldElement eCFieldElement1 = paramECCurve.fromBigInteger(ECConstants.ZERO);
    ECFieldElement eCFieldElement2 = null;
    ECFieldElement eCFieldElement3 = null;
    Random random = new Random();
    int i = paramECFieldElement.getFieldSize();
    while (true) {
      ECFieldElement eCFieldElement4 = paramECCurve.fromBigInteger(new BigInteger(i, random));
      eCFieldElement2 = eCFieldElement1;
      ECFieldElement eCFieldElement5 = paramECFieldElement;
      for (byte b = 1; b <= i - 1; b++) {
        ECFieldElement eCFieldElement = eCFieldElement5.square();
        eCFieldElement2 = eCFieldElement2.square().add(eCFieldElement.multiply(eCFieldElement4));
        eCFieldElement5 = eCFieldElement.add(paramECFieldElement);
      } 
      if (!eCFieldElement5.isZero())
        return null; 
      eCFieldElement3 = eCFieldElement2.square().add(eCFieldElement2);
      if (!eCFieldElement3.isZero())
        return eCFieldElement2; 
    } 
  }
  
  public static byte[] encodePoint(ECPoint paramECPoint) {
    paramECPoint = paramECPoint.normalize();
    ECFieldElement eCFieldElement = paramECPoint.getAffineXCoord();
    byte[] arrayOfByte = eCFieldElement.getEncoded();
    if (!eCFieldElement.isZero()) {
      ECFieldElement eCFieldElement1 = paramECPoint.getAffineYCoord().divide(eCFieldElement);
      if (trace(eCFieldElement1).isOne()) {
        arrayOfByte[arrayOfByte.length - 1] = (byte)(arrayOfByte[arrayOfByte.length - 1] | 0x1);
      } else {
        arrayOfByte[arrayOfByte.length - 1] = (byte)(arrayOfByte[arrayOfByte.length - 1] & 0xFE);
      } 
    } 
    return arrayOfByte;
  }
  
  public static ECPoint decodePoint(ECCurve paramECCurve, byte[] paramArrayOfbyte) {
    ECFieldElement eCFieldElement1 = paramECCurve.fromBigInteger(BigInteger.valueOf((paramArrayOfbyte[paramArrayOfbyte.length - 1] & 0x1)));
    ECFieldElement eCFieldElement2 = paramECCurve.fromBigInteger(new BigInteger(1, paramArrayOfbyte));
    if (!trace(eCFieldElement2).equals(paramECCurve.getA()))
      eCFieldElement2 = eCFieldElement2.addOne(); 
    ECFieldElement eCFieldElement3 = null;
    if (eCFieldElement2.isZero()) {
      eCFieldElement3 = paramECCurve.getB().sqrt();
    } else {
      ECFieldElement eCFieldElement4 = eCFieldElement2.square().invert().multiply(paramECCurve.getB()).add(paramECCurve.getA()).add(eCFieldElement2);
      ECFieldElement eCFieldElement5 = solveQuadraticEquation(paramECCurve, eCFieldElement4);
      if (eCFieldElement5 != null) {
        if (!trace(eCFieldElement5).equals(eCFieldElement1))
          eCFieldElement5 = eCFieldElement5.addOne(); 
        eCFieldElement3 = eCFieldElement2.multiply(eCFieldElement5);
      } 
    } 
    if (eCFieldElement3 == null)
      throw new IllegalArgumentException("Invalid point compression"); 
    return paramECCurve.validatePoint(eCFieldElement2.toBigInteger(), eCFieldElement3.toBigInteger());
  }
}
