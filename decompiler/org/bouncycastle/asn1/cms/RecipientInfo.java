package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class RecipientInfo extends ASN1Object implements ASN1Choice {
  ASN1Encodable info;
  
  public RecipientInfo(KeyTransRecipientInfo paramKeyTransRecipientInfo) {
    this.info = (ASN1Encodable)paramKeyTransRecipientInfo;
  }
  
  public RecipientInfo(KeyAgreeRecipientInfo paramKeyAgreeRecipientInfo) {
    this.info = (ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)paramKeyAgreeRecipientInfo);
  }
  
  public RecipientInfo(KEKRecipientInfo paramKEKRecipientInfo) {
    this.info = (ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)paramKEKRecipientInfo);
  }
  
  public RecipientInfo(PasswordRecipientInfo paramPasswordRecipientInfo) {
    this.info = (ASN1Encodable)new DERTaggedObject(false, 3, (ASN1Encodable)paramPasswordRecipientInfo);
  }
  
  public RecipientInfo(OtherRecipientInfo paramOtherRecipientInfo) {
    this.info = (ASN1Encodable)new DERTaggedObject(false, 4, (ASN1Encodable)paramOtherRecipientInfo);
  }
  
  public RecipientInfo(ASN1Primitive paramASN1Primitive) {
    this.info = (ASN1Encodable)paramASN1Primitive;
  }
  
  public static RecipientInfo getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof RecipientInfo)
      return (RecipientInfo)paramObject; 
    if (paramObject instanceof org.bouncycastle.asn1.ASN1Sequence)
      return new RecipientInfo((ASN1Primitive)paramObject); 
    if (paramObject instanceof ASN1TaggedObject)
      return new RecipientInfo((ASN1Primitive)paramObject); 
    throw new IllegalArgumentException("unknown object in factory: " + paramObject.getClass().getName());
  }
  
  public ASN1Integer getVersion() {
    if (this.info instanceof ASN1TaggedObject) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)this.info;
      switch (aSN1TaggedObject.getTagNo()) {
        case 1:
          return KeyAgreeRecipientInfo.getInstance(aSN1TaggedObject, false).getVersion();
        case 2:
          return getKEKInfo(aSN1TaggedObject).getVersion();
        case 3:
          return PasswordRecipientInfo.getInstance(aSN1TaggedObject, false).getVersion();
        case 4:
          return new ASN1Integer(0L);
      } 
      throw new IllegalStateException("unknown tag");
    } 
    return KeyTransRecipientInfo.getInstance(this.info).getVersion();
  }
  
  public boolean isTagged() {
    return this.info instanceof ASN1TaggedObject;
  }
  
  public ASN1Encodable getInfo() {
    if (this.info instanceof ASN1TaggedObject) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)this.info;
      switch (aSN1TaggedObject.getTagNo()) {
        case 1:
          return (ASN1Encodable)KeyAgreeRecipientInfo.getInstance(aSN1TaggedObject, false);
        case 2:
          return (ASN1Encodable)getKEKInfo(aSN1TaggedObject);
        case 3:
          return (ASN1Encodable)PasswordRecipientInfo.getInstance(aSN1TaggedObject, false);
        case 4:
          return (ASN1Encodable)OtherRecipientInfo.getInstance(aSN1TaggedObject, false);
      } 
      throw new IllegalStateException("unknown tag");
    } 
    return (ASN1Encodable)KeyTransRecipientInfo.getInstance(this.info);
  }
  
  private KEKRecipientInfo getKEKInfo(ASN1TaggedObject paramASN1TaggedObject) {
    return paramASN1TaggedObject.isExplicit() ? KEKRecipientInfo.getInstance(paramASN1TaggedObject, true) : KEKRecipientInfo.getInstance(paramASN1TaggedObject, false);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.info.toASN1Primitive();
  }
}
