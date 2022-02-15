package org.bouncycastle.jce;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

public class ECGOST3410NamedCurveTable {
  public static ECNamedCurveParameterSpec getParameterSpec(String paramString) {
    ECDomainParameters eCDomainParameters = ECGOST3410NamedCurves.getByName(paramString);
    if (eCDomainParameters == null)
      try {
        eCDomainParameters = ECGOST3410NamedCurves.getByOID(new ASN1ObjectIdentifier(paramString));
      } catch (IllegalArgumentException illegalArgumentException) {
        return null;
      }  
    return (eCDomainParameters == null) ? null : new ECNamedCurveParameterSpec(paramString, eCDomainParameters.getCurve(), eCDomainParameters.getG(), eCDomainParameters.getN(), eCDomainParameters.getH(), eCDomainParameters.getSeed());
  }
  
  public static Enumeration getNames() {
    return ECGOST3410NamedCurves.getNames();
  }
}
