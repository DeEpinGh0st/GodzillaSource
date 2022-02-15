package org.bouncycastle.asn1.x500;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class X500Name extends ASN1Object implements ASN1Choice {
  private static X500NameStyle defaultStyle = BCStyle.INSTANCE;
  
  private boolean isHashCodeCalculated;
  
  private int hashCodeValue;
  
  private X500NameStyle style;
  
  private RDN[] rdns;
  
  public X500Name(X500NameStyle paramX500NameStyle, X500Name paramX500Name) {
    this.rdns = paramX500Name.rdns;
    this.style = paramX500NameStyle;
  }
  
  public static X500Name getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, true));
  }
  
  public static X500Name getInstance(Object paramObject) {
    return (paramObject instanceof X500Name) ? (X500Name)paramObject : ((paramObject != null) ? new X500Name(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static X500Name getInstance(X500NameStyle paramX500NameStyle, Object paramObject) {
    return (paramObject instanceof X500Name) ? new X500Name(paramX500NameStyle, (X500Name)paramObject) : ((paramObject != null) ? new X500Name(paramX500NameStyle, ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private X500Name(ASN1Sequence paramASN1Sequence) {
    this(defaultStyle, paramASN1Sequence);
  }
  
  private X500Name(X500NameStyle paramX500NameStyle, ASN1Sequence paramASN1Sequence) {
    this.style = paramX500NameStyle;
    this.rdns = new RDN[paramASN1Sequence.size()];
    byte b = 0;
    Enumeration enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements())
      this.rdns[b++] = RDN.getInstance(enumeration.nextElement()); 
  }
  
  public X500Name(RDN[] paramArrayOfRDN) {
    this(defaultStyle, paramArrayOfRDN);
  }
  
  public X500Name(X500NameStyle paramX500NameStyle, RDN[] paramArrayOfRDN) {
    this.rdns = paramArrayOfRDN;
    this.style = paramX500NameStyle;
  }
  
  public X500Name(String paramString) {
    this(defaultStyle, paramString);
  }
  
  public X500Name(X500NameStyle paramX500NameStyle, String paramString) {
    this(paramX500NameStyle.fromString(paramString));
    this.style = paramX500NameStyle;
  }
  
  public RDN[] getRDNs() {
    RDN[] arrayOfRDN = new RDN[this.rdns.length];
    System.arraycopy(this.rdns, 0, arrayOfRDN, 0, arrayOfRDN.length);
    return arrayOfRDN;
  }
  
  public ASN1ObjectIdentifier[] getAttributeTypes() {
    int i = 0;
    for (byte b1 = 0; b1 != this.rdns.length; b1++) {
      RDN rDN = this.rdns[b1];
      i += rDN.size();
    } 
    ASN1ObjectIdentifier[] arrayOfASN1ObjectIdentifier = new ASN1ObjectIdentifier[i];
    i = 0;
    for (byte b2 = 0; b2 != this.rdns.length; b2++) {
      RDN rDN = this.rdns[b2];
      if (rDN.isMultiValued()) {
        AttributeTypeAndValue[] arrayOfAttributeTypeAndValue = rDN.getTypesAndValues();
        for (byte b = 0; b != arrayOfAttributeTypeAndValue.length; b++)
          arrayOfASN1ObjectIdentifier[i++] = arrayOfAttributeTypeAndValue[b].getType(); 
      } else if (rDN.size() != 0) {
        arrayOfASN1ObjectIdentifier[i++] = rDN.getFirst().getType();
      } 
    } 
    return arrayOfASN1ObjectIdentifier;
  }
  
  public RDN[] getRDNs(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    RDN[] arrayOfRDN1 = new RDN[this.rdns.length];
    byte b1 = 0;
    for (byte b2 = 0; b2 != this.rdns.length; b2++) {
      RDN rDN = this.rdns[b2];
      if (rDN.isMultiValued()) {
        AttributeTypeAndValue[] arrayOfAttributeTypeAndValue = rDN.getTypesAndValues();
        for (byte b = 0; b != arrayOfAttributeTypeAndValue.length; b++) {
          if (arrayOfAttributeTypeAndValue[b].getType().equals(paramASN1ObjectIdentifier)) {
            arrayOfRDN1[b1++] = rDN;
            break;
          } 
        } 
      } else if (rDN.getFirst().getType().equals(paramASN1ObjectIdentifier)) {
        arrayOfRDN1[b1++] = rDN;
      } 
    } 
    RDN[] arrayOfRDN2 = new RDN[b1];
    System.arraycopy(arrayOfRDN1, 0, arrayOfRDN2, 0, arrayOfRDN2.length);
    return arrayOfRDN2;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence((ASN1Encodable[])this.rdns);
  }
  
  public int hashCode() {
    if (this.isHashCodeCalculated)
      return this.hashCodeValue; 
    this.isHashCodeCalculated = true;
    this.hashCodeValue = this.style.calculateHashCode(this);
    return this.hashCodeValue;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof X500Name) && !(paramObject instanceof ASN1Sequence))
      return false; 
    ASN1Primitive aSN1Primitive = ((ASN1Encodable)paramObject).toASN1Primitive();
    if (toASN1Primitive().equals(aSN1Primitive))
      return true; 
    try {
      return this.style.areEqual(this, new X500Name(ASN1Sequence.getInstance(((ASN1Encodable)paramObject).toASN1Primitive())));
    } catch (Exception exception) {
      return false;
    } 
  }
  
  public String toString() {
    return this.style.toString(this);
  }
  
  public static void setDefaultStyle(X500NameStyle paramX500NameStyle) {
    if (paramX500NameStyle == null)
      throw new NullPointerException("cannot set style to null"); 
    defaultStyle = paramX500NameStyle;
  }
  
  public static X500NameStyle getDefaultStyle() {
    return defaultStyle;
  }
}
