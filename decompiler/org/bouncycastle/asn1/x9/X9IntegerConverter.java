package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;

public class X9IntegerConverter {
  public int getByteLength(ECCurve paramECCurve) {
    return (paramECCurve.getFieldSize() + 7) / 8;
  }
  
  public int getByteLength(ECFieldElement paramECFieldElement) {
    return (paramECFieldElement.getFieldSize() + 7) / 8;
  }
  
  public byte[] integerToBytes(BigInteger paramBigInteger, int paramInt) {
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    if (paramInt < arrayOfByte.length) {
      byte[] arrayOfByte1 = new byte[paramInt];
      System.arraycopy(arrayOfByte, arrayOfByte.length - arrayOfByte1.length, arrayOfByte1, 0, arrayOfByte1.length);
      return arrayOfByte1;
    } 
    if (paramInt > arrayOfByte.length) {
      byte[] arrayOfByte1 = new byte[paramInt];
      System.arraycopy(arrayOfByte, 0, arrayOfByte1, arrayOfByte1.length - arrayOfByte.length, arrayOfByte.length);
      return arrayOfByte1;
    } 
    return arrayOfByte;
  }
}
