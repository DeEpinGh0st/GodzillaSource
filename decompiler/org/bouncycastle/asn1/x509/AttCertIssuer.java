package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class AttCertIssuer extends ASN1Object implements ASN1Choice {
  ASN1Encodable obj;
  
  ASN1Primitive choiceObj;
  
  public static AttCertIssuer getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof AttCertIssuer)
      return (AttCertIssuer)paramObject; 
    if (paramObject instanceof V2Form)
      return new AttCertIssuer(V2Form.getInstance(paramObject)); 
    if (paramObject instanceof GeneralNames)
      return new AttCertIssuer((GeneralNames)paramObject); 
    if (paramObject instanceof ASN1TaggedObject)
      return new AttCertIssuer(V2Form.getInstance((ASN1TaggedObject)paramObject, false)); 
    if (paramObject instanceof org.bouncycastle.asn1.ASN1Sequence)
      return new AttCertIssuer(GeneralNames.getInstance(paramObject)); 
    throw new IllegalArgumentException("unknown object in factory: " + paramObject.getClass().getName());
  }
  
  public static AttCertIssuer getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(paramASN1TaggedObject.getObject());
  }
  
  public AttCertIssuer(GeneralNames paramGeneralNames) {
    this.obj = (ASN1Encodable)paramGeneralNames;
    this.choiceObj = this.obj.toASN1Primitive();
  }
  
  public AttCertIssuer(V2Form paramV2Form) {
    this.obj = (ASN1Encodable)paramV2Form;
    this.choiceObj = (ASN1Primitive)new DERTaggedObject(false, 0, this.obj);
  }
  
  public ASN1Encodable getIssuer() {
    return this.obj;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.choiceObj;
  }
}
