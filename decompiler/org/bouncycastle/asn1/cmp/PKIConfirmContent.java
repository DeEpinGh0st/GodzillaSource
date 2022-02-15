package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;

public class PKIConfirmContent extends ASN1Object {
  private ASN1Null val;
  
  private PKIConfirmContent(ASN1Null paramASN1Null) {
    this.val = paramASN1Null;
  }
  
  public static PKIConfirmContent getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof PKIConfirmContent)
      return (PKIConfirmContent)paramObject; 
    if (paramObject instanceof ASN1Null)
      return new PKIConfirmContent((ASN1Null)paramObject); 
    throw new IllegalArgumentException("Invalid object: " + paramObject.getClass().getName());
  }
  
  public PKIConfirmContent() {
    this.val = (ASN1Null)DERNull.INSTANCE;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.val;
  }
}
