package org.bouncycastle.asn1.nist;

import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.util.Strings;

public class NISTNamedCurves {
  static final Hashtable objIds = new Hashtable<Object, Object>();
  
  static final Hashtable names = new Hashtable<Object, Object>();
  
  static void defineCurve(String paramString, ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    objIds.put(paramString, paramASN1ObjectIdentifier);
    names.put(paramASN1ObjectIdentifier, paramString);
  }
  
  public static X9ECParameters getByName(String paramString) {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)objIds.get(Strings.toUpperCase(paramString));
    return (aSN1ObjectIdentifier != null) ? getByOID(aSN1ObjectIdentifier) : null;
  }
  
  public static X9ECParameters getByOID(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return SECNamedCurves.getByOID(paramASN1ObjectIdentifier);
  }
  
  public static ASN1ObjectIdentifier getOID(String paramString) {
    return (ASN1ObjectIdentifier)objIds.get(Strings.toUpperCase(paramString));
  }
  
  public static String getName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (String)names.get(paramASN1ObjectIdentifier);
  }
  
  public static Enumeration getNames() {
    return objIds.keys();
  }
  
  static {
    defineCurve("B-571", SECObjectIdentifiers.sect571r1);
    defineCurve("B-409", SECObjectIdentifiers.sect409r1);
    defineCurve("B-283", SECObjectIdentifiers.sect283r1);
    defineCurve("B-233", SECObjectIdentifiers.sect233r1);
    defineCurve("B-163", SECObjectIdentifiers.sect163r2);
    defineCurve("K-571", SECObjectIdentifiers.sect571k1);
    defineCurve("K-409", SECObjectIdentifiers.sect409k1);
    defineCurve("K-283", SECObjectIdentifiers.sect283k1);
    defineCurve("K-233", SECObjectIdentifiers.sect233k1);
    defineCurve("K-163", SECObjectIdentifiers.sect163k1);
    defineCurve("P-521", SECObjectIdentifiers.secp521r1);
    defineCurve("P-384", SECObjectIdentifiers.secp384r1);
    defineCurve("P-256", SECObjectIdentifiers.secp256r1);
    defineCurve("P-224", SECObjectIdentifiers.secp224r1);
    defineCurve("P-192", SECObjectIdentifiers.secp192r1);
  }
}
