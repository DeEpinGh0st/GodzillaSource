package org.bouncycastle.asn1.x500;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface X500NameStyle {
  ASN1Encodable stringToValue(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString);
  
  ASN1ObjectIdentifier attrNameToOID(String paramString);
  
  RDN[] fromString(String paramString);
  
  boolean areEqual(X500Name paramX500Name1, X500Name paramX500Name2);
  
  int calculateHashCode(X500Name paramX500Name);
  
  String toString(X500Name paramX500Name);
  
  String oidToDisplayName(ASN1ObjectIdentifier paramASN1ObjectIdentifier);
  
  String[] oidToAttrNames(ASN1ObjectIdentifier paramASN1ObjectIdentifier);
}
