package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERUTF8String;

public class X509DefaultEntryConverter extends X509NameEntryConverter {
  public ASN1Primitive getConvertedValue(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    if (paramString.length() != 0 && paramString.charAt(0) == '#')
      try {
        return convertHexEncoded(paramString, 1);
      } catch (IOException iOException) {
        throw new RuntimeException("can't recode value for oid " + paramASN1ObjectIdentifier.getId());
      }  
    if (paramString.length() != 0 && paramString.charAt(0) == '\\')
      paramString = paramString.substring(1); 
    return (ASN1Primitive)((paramASN1ObjectIdentifier.equals(X509Name.EmailAddress) || paramASN1ObjectIdentifier.equals(X509Name.DC)) ? new DERIA5String(paramString) : (paramASN1ObjectIdentifier.equals(X509Name.DATE_OF_BIRTH) ? new DERGeneralizedTime(paramString) : ((paramASN1ObjectIdentifier.equals(X509Name.C) || paramASN1ObjectIdentifier.equals(X509Name.SN) || paramASN1ObjectIdentifier.equals(X509Name.DN_QUALIFIER) || paramASN1ObjectIdentifier.equals(X509Name.TELEPHONE_NUMBER)) ? new DERPrintableString(paramString) : new DERUTF8String(paramString))));
  }
}
