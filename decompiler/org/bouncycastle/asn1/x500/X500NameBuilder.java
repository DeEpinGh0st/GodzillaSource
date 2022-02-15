package org.bouncycastle.asn1.x500;

import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class X500NameBuilder {
  private X500NameStyle template;
  
  private Vector rdns = new Vector();
  
  public X500NameBuilder() {
    this(BCStyle.INSTANCE);
  }
  
  public X500NameBuilder(X500NameStyle paramX500NameStyle) {
    this.template = paramX500NameStyle;
  }
  
  public X500NameBuilder addRDN(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    addRDN(paramASN1ObjectIdentifier, this.template.stringToValue(paramASN1ObjectIdentifier, paramString));
    return this;
  }
  
  public X500NameBuilder addRDN(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.rdns.addElement(new RDN(paramASN1ObjectIdentifier, paramASN1Encodable));
    return this;
  }
  
  public X500NameBuilder addRDN(AttributeTypeAndValue paramAttributeTypeAndValue) {
    this.rdns.addElement(new RDN(paramAttributeTypeAndValue));
    return this;
  }
  
  public X500NameBuilder addMultiValuedRDN(ASN1ObjectIdentifier[] paramArrayOfASN1ObjectIdentifier, String[] paramArrayOfString) {
    ASN1Encodable[] arrayOfASN1Encodable = new ASN1Encodable[paramArrayOfString.length];
    for (byte b = 0; b != arrayOfASN1Encodable.length; b++)
      arrayOfASN1Encodable[b] = this.template.stringToValue(paramArrayOfASN1ObjectIdentifier[b], paramArrayOfString[b]); 
    return addMultiValuedRDN(paramArrayOfASN1ObjectIdentifier, arrayOfASN1Encodable);
  }
  
  public X500NameBuilder addMultiValuedRDN(ASN1ObjectIdentifier[] paramArrayOfASN1ObjectIdentifier, ASN1Encodable[] paramArrayOfASN1Encodable) {
    AttributeTypeAndValue[] arrayOfAttributeTypeAndValue = new AttributeTypeAndValue[paramArrayOfASN1ObjectIdentifier.length];
    for (byte b = 0; b != paramArrayOfASN1ObjectIdentifier.length; b++)
      arrayOfAttributeTypeAndValue[b] = new AttributeTypeAndValue(paramArrayOfASN1ObjectIdentifier[b], paramArrayOfASN1Encodable[b]); 
    return addMultiValuedRDN(arrayOfAttributeTypeAndValue);
  }
  
  public X500NameBuilder addMultiValuedRDN(AttributeTypeAndValue[] paramArrayOfAttributeTypeAndValue) {
    this.rdns.addElement(new RDN(paramArrayOfAttributeTypeAndValue));
    return this;
  }
  
  public X500Name build() {
    RDN[] arrayOfRDN = new RDN[this.rdns.size()];
    for (byte b = 0; b != arrayOfRDN.length; b++)
      arrayOfRDN[b] = this.rdns.elementAt(b); 
    return new X500Name(this.template, arrayOfRDN);
  }
}
