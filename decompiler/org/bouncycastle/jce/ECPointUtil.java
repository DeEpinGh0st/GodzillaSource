package org.bouncycastle.jce;

import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class ECPointUtil {
  public static ECPoint decodePoint(EllipticCurve paramEllipticCurve, byte[] paramArrayOfbyte) {
    ECCurve.F2m f2m;
    ECCurve.Fp fp = null;
    if (paramEllipticCurve.getField() instanceof ECFieldFp) {
      fp = new ECCurve.Fp(((ECFieldFp)paramEllipticCurve.getField()).getP(), paramEllipticCurve.getA(), paramEllipticCurve.getB());
    } else {
      int[] arrayOfInt = ((ECFieldF2m)paramEllipticCurve.getField()).getMidTermsOfReductionPolynomial();
      if (arrayOfInt.length == 3) {
        f2m = new ECCurve.F2m(((ECFieldF2m)paramEllipticCurve.getField()).getM(), arrayOfInt[2], arrayOfInt[1], arrayOfInt[0], paramEllipticCurve.getA(), paramEllipticCurve.getB());
      } else {
        f2m = new ECCurve.F2m(((ECFieldF2m)paramEllipticCurve.getField()).getM(), arrayOfInt[0], paramEllipticCurve.getA(), paramEllipticCurve.getB());
      } 
    } 
    ECPoint eCPoint = f2m.decodePoint(paramArrayOfbyte);
    return new ECPoint(eCPoint.getAffineXCoord().toBigInteger(), eCPoint.getAffineYCoord().toBigInteger());
  }
}
