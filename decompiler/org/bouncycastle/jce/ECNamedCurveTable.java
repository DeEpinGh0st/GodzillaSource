package org.bouncycastle.jce;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

public class ECNamedCurveTable {
  public static ECNamedCurveParameterSpec getParameterSpec(String paramString) {
    X9ECParameters x9ECParameters = CustomNamedCurves.getByName(paramString);
    if (x9ECParameters == null) {
      try {
        x9ECParameters = CustomNamedCurves.getByOID(new ASN1ObjectIdentifier(paramString));
      } catch (IllegalArgumentException illegalArgumentException) {}
      if (x9ECParameters == null) {
        x9ECParameters = org.bouncycastle.asn1.x9.ECNamedCurveTable.getByName(paramString);
        if (x9ECParameters == null)
          try {
            x9ECParameters = org.bouncycastle.asn1.x9.ECNamedCurveTable.getByOID(new ASN1ObjectIdentifier(paramString));
          } catch (IllegalArgumentException illegalArgumentException) {} 
      } 
    } 
    return (x9ECParameters == null) ? null : new ECNamedCurveParameterSpec(paramString, x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), x9ECParameters.getSeed());
  }
  
  public static Enumeration getNames() {
    return org.bouncycastle.asn1.x9.ECNamedCurveTable.getNames();
  }
}
