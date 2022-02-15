package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class WTauNafMultiplier extends AbstractECMultiplier {
  static final String PRECOMP_NAME = "bc_wtnaf";
  
  protected ECPoint multiplyPositive(ECPoint paramECPoint, BigInteger paramBigInteger) {
    if (!(paramECPoint instanceof ECPoint.AbstractF2m))
      throw new IllegalArgumentException("Only ECPoint.AbstractF2m can be used in WTauNafMultiplier"); 
    ECPoint.AbstractF2m abstractF2m = (ECPoint.AbstractF2m)paramECPoint;
    ECCurve.AbstractF2m abstractF2m1 = (ECCurve.AbstractF2m)abstractF2m.getCurve();
    int i = abstractF2m1.getFieldSize();
    byte b1 = abstractF2m1.getA().toBigInteger().byteValue();
    byte b2 = Tnaf.getMu(b1);
    BigInteger[] arrayOfBigInteger = abstractF2m1.getSi();
    ZTauElement zTauElement = Tnaf.partModReduction(paramBigInteger, i, b1, arrayOfBigInteger, b2, (byte)10);
    return multiplyWTnaf(abstractF2m, zTauElement, abstractF2m1.getPreCompInfo(abstractF2m, "bc_wtnaf"), b1, b2);
  }
  
  private ECPoint.AbstractF2m multiplyWTnaf(ECPoint.AbstractF2m paramAbstractF2m, ZTauElement paramZTauElement, PreCompInfo paramPreCompInfo, byte paramByte1, byte paramByte2) {
    ZTauElement[] arrayOfZTauElement = (paramByte1 == 0) ? Tnaf.alpha0 : Tnaf.alpha1;
    BigInteger bigInteger = Tnaf.getTw(paramByte2, 4);
    byte[] arrayOfByte = Tnaf.tauAdicWNaf(paramByte2, paramZTauElement, (byte)4, BigInteger.valueOf(16L), bigInteger, arrayOfZTauElement);
    return multiplyFromWTnaf(paramAbstractF2m, arrayOfByte, paramPreCompInfo);
  }
  
  private static ECPoint.AbstractF2m multiplyFromWTnaf(ECPoint.AbstractF2m paramAbstractF2m, byte[] paramArrayOfbyte, PreCompInfo paramPreCompInfo) {
    ECPoint.AbstractF2m[] arrayOfAbstractF2m1;
    ECCurve.AbstractF2m abstractF2m = (ECCurve.AbstractF2m)paramAbstractF2m.getCurve();
    byte b = abstractF2m.getA().toBigInteger().byteValue();
    if (paramPreCompInfo == null || !(paramPreCompInfo instanceof WTauNafPreCompInfo)) {
      arrayOfAbstractF2m1 = Tnaf.getPreComp(paramAbstractF2m, b);
      WTauNafPreCompInfo wTauNafPreCompInfo = new WTauNafPreCompInfo();
      wTauNafPreCompInfo.setPreComp(arrayOfAbstractF2m1);
      abstractF2m.setPreCompInfo(paramAbstractF2m, "bc_wtnaf", wTauNafPreCompInfo);
    } else {
      arrayOfAbstractF2m1 = ((WTauNafPreCompInfo)paramPreCompInfo).getPreComp();
    } 
    ECPoint.AbstractF2m[] arrayOfAbstractF2m2 = new ECPoint.AbstractF2m[arrayOfAbstractF2m1.length];
    for (byte b1 = 0; b1 < arrayOfAbstractF2m1.length; b1++)
      arrayOfAbstractF2m2[b1] = (ECPoint.AbstractF2m)arrayOfAbstractF2m1[b1].negate(); 
    ECPoint.AbstractF2m abstractF2m1 = (ECPoint.AbstractF2m)paramAbstractF2m.getCurve().getInfinity();
    byte b2 = 0;
    for (int i = paramArrayOfbyte.length - 1; i >= 0; i--) {
      b2++;
      byte b3 = paramArrayOfbyte[i];
      if (b3 != 0) {
        abstractF2m1 = abstractF2m1.tauPow(b2);
        b2 = 0;
        ECPoint.AbstractF2m abstractF2m2 = (b3 > 0) ? arrayOfAbstractF2m1[b3 >>> 1] : arrayOfAbstractF2m2[-b3 >>> 1];
        abstractF2m1 = (ECPoint.AbstractF2m)abstractF2m1.add(abstractF2m2);
      } 
    } 
    if (b2 > 0)
      abstractF2m1 = abstractF2m1.tauPow(b2); 
    return abstractF2m1;
  }
}
