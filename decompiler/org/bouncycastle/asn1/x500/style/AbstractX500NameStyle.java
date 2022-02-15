package org.bouncycastle.asn1.x500.style;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;

public abstract class AbstractX500NameStyle implements X500NameStyle {
  public static Hashtable copyHashTable(Hashtable paramHashtable) {
    Hashtable<Object, Object> hashtable = new Hashtable<Object, Object>();
    Enumeration<Object> enumeration = paramHashtable.keys();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      hashtable.put(object, paramHashtable.get(object));
    } 
    return hashtable;
  }
  
  private int calcHashCode(ASN1Encodable paramASN1Encodable) {
    String str = IETFUtils.valueToString(paramASN1Encodable);
    str = IETFUtils.canonicalize(str);
    return str.hashCode();
  }
  
  public int calculateHashCode(X500Name paramX500Name) {
    int i = 0;
    RDN[] arrayOfRDN = paramX500Name.getRDNs();
    for (byte b = 0; b != arrayOfRDN.length; b++) {
      if (arrayOfRDN[b].isMultiValued()) {
        AttributeTypeAndValue[] arrayOfAttributeTypeAndValue = arrayOfRDN[b].getTypesAndValues();
        for (byte b1 = 0; b1 != arrayOfAttributeTypeAndValue.length; b1++) {
          i ^= arrayOfAttributeTypeAndValue[b1].getType().hashCode();
          i ^= calcHashCode(arrayOfAttributeTypeAndValue[b1].getValue());
        } 
      } else {
        i ^= arrayOfRDN[b].getFirst().getType().hashCode();
        i ^= calcHashCode(arrayOfRDN[b].getFirst().getValue());
      } 
    } 
    return i;
  }
  
  public ASN1Encodable stringToValue(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    if (paramString.length() != 0 && paramString.charAt(0) == '#')
      try {
        return IETFUtils.valueFromHexString(paramString, 1);
      } catch (IOException iOException) {
        throw new ASN1ParsingException("can't recode value for oid " + paramASN1ObjectIdentifier.getId());
      }  
    if (paramString.length() != 0 && paramString.charAt(0) == '\\')
      paramString = paramString.substring(1); 
    return encodeStringValue(paramASN1ObjectIdentifier, paramString);
  }
  
  protected ASN1Encodable encodeStringValue(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    return (ASN1Encodable)new DERUTF8String(paramString);
  }
  
  public boolean areEqual(X500Name paramX500Name1, X500Name paramX500Name2) {
    RDN[] arrayOfRDN1 = paramX500Name1.getRDNs();
    RDN[] arrayOfRDN2 = paramX500Name2.getRDNs();
    if (arrayOfRDN1.length != arrayOfRDN2.length)
      return false; 
    boolean bool = false;
    if (arrayOfRDN1[0].getFirst() != null && arrayOfRDN2[0].getFirst() != null)
      bool = !arrayOfRDN1[0].getFirst().getType().equals(arrayOfRDN2[0].getFirst().getType()) ? true : false; 
    for (byte b = 0; b != arrayOfRDN1.length; b++) {
      if (!foundMatch(bool, arrayOfRDN1[b], arrayOfRDN2))
        return false; 
    } 
    return true;
  }
  
  private boolean foundMatch(boolean paramBoolean, RDN paramRDN, RDN[] paramArrayOfRDN) {
    if (paramBoolean) {
      for (int i = paramArrayOfRDN.length - 1; i >= 0; i--) {
        if (paramArrayOfRDN[i] != null && rdnAreEqual(paramRDN, paramArrayOfRDN[i])) {
          paramArrayOfRDN[i] = null;
          return true;
        } 
      } 
    } else {
      for (byte b = 0; b != paramArrayOfRDN.length; b++) {
        if (paramArrayOfRDN[b] != null && rdnAreEqual(paramRDN, paramArrayOfRDN[b])) {
          paramArrayOfRDN[b] = null;
          return true;
        } 
      } 
    } 
    return false;
  }
  
  protected boolean rdnAreEqual(RDN paramRDN1, RDN paramRDN2) {
    return IETFUtils.rDNAreEqual(paramRDN1, paramRDN2);
  }
}
