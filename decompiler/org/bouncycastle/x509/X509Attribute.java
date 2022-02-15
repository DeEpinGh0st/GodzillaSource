package org.bouncycastle.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.Attribute;

public class X509Attribute extends ASN1Object {
  Attribute attr;
  
  X509Attribute(ASN1Encodable paramASN1Encodable) {
    this.attr = Attribute.getInstance(paramASN1Encodable);
  }
  
  public X509Attribute(String paramString, ASN1Encodable paramASN1Encodable) {
    this.attr = new Attribute(new ASN1ObjectIdentifier(paramString), (ASN1Set)new DERSet(paramASN1Encodable));
  }
  
  public X509Attribute(String paramString, ASN1EncodableVector paramASN1EncodableVector) {
    this.attr = new Attribute(new ASN1ObjectIdentifier(paramString), (ASN1Set)new DERSet(paramASN1EncodableVector));
  }
  
  public String getOID() {
    return this.attr.getAttrType().getId();
  }
  
  public ASN1Encodable[] getValues() {
    ASN1Set aSN1Set = this.attr.getAttrValues();
    ASN1Encodable[] arrayOfASN1Encodable = new ASN1Encodable[aSN1Set.size()];
    for (byte b = 0; b != aSN1Set.size(); b++)
      arrayOfASN1Encodable[b] = aSN1Set.getObjectAt(b); 
    return arrayOfASN1Encodable;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.attr.toASN1Primitive();
  }
}
