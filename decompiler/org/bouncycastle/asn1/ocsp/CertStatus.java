package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;

public class CertStatus extends ASN1Object implements ASN1Choice {
  private int tagNo = 0;
  
  private ASN1Encodable value;
  
  public CertStatus() {
    this.value = (ASN1Encodable)DERNull.INSTANCE;
  }
  
  public CertStatus(RevokedInfo paramRevokedInfo) {
    this.value = (ASN1Encodable)paramRevokedInfo;
  }
  
  public CertStatus(int paramInt, ASN1Encodable paramASN1Encodable) {
    this.value = paramASN1Encodable;
  }
  
  private CertStatus(ASN1TaggedObject paramASN1TaggedObject) {
    switch (paramASN1TaggedObject.getTagNo()) {
      case 0:
        this.value = (ASN1Encodable)DERNull.INSTANCE;
        return;
      case 1:
        this.value = (ASN1Encodable)RevokedInfo.getInstance(paramASN1TaggedObject, false);
        return;
      case 2:
        this.value = (ASN1Encodable)DERNull.INSTANCE;
        return;
    } 
    throw new IllegalArgumentException("Unknown tag encountered: " + paramASN1TaggedObject.getTagNo());
  }
  
  public static CertStatus getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof CertStatus)
      return (CertStatus)paramObject; 
    if (paramObject instanceof ASN1TaggedObject)
      return new CertStatus((ASN1TaggedObject)paramObject); 
    throw new IllegalArgumentException("unknown object in factory: " + paramObject.getClass().getName());
  }
  
  public static CertStatus getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(paramASN1TaggedObject.getObject());
  }
  
  public int getTagNo() {
    return this.tagNo;
  }
  
  public ASN1Encodable getStatus() {
    return this.value;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERTaggedObject(false, this.tagNo, this.value);
  }
}
