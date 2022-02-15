package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.EnvelopedData;

public class POPOPrivKey extends ASN1Object implements ASN1Choice {
  public static final int thisMessage = 0;
  
  public static final int subsequentMessage = 1;
  
  public static final int dhMAC = 2;
  
  public static final int agreeMAC = 3;
  
  public static final int encryptedKey = 4;
  
  private int tagNo;
  
  private ASN1Encodable obj;
  
  private POPOPrivKey(ASN1TaggedObject paramASN1TaggedObject) {
    this.tagNo = paramASN1TaggedObject.getTagNo();
    switch (this.tagNo) {
      case 0:
        this.obj = (ASN1Encodable)DERBitString.getInstance(paramASN1TaggedObject, false);
        return;
      case 1:
        this.obj = (ASN1Encodable)SubsequentMessage.valueOf(ASN1Integer.getInstance(paramASN1TaggedObject, false).getValue().intValue());
        return;
      case 2:
        this.obj = (ASN1Encodable)DERBitString.getInstance(paramASN1TaggedObject, false);
        return;
      case 3:
        this.obj = (ASN1Encodable)PKMACValue.getInstance(paramASN1TaggedObject, false);
        return;
      case 4:
        this.obj = (ASN1Encodable)EnvelopedData.getInstance(paramASN1TaggedObject, false);
        return;
    } 
    throw new IllegalArgumentException("unknown tag in POPOPrivKey");
  }
  
  public static POPOPrivKey getInstance(Object paramObject) {
    return (paramObject instanceof POPOPrivKey) ? (POPOPrivKey)paramObject : ((paramObject != null) ? new POPOPrivKey(ASN1TaggedObject.getInstance(paramObject)) : null);
  }
  
  public static POPOPrivKey getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1TaggedObject.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public POPOPrivKey(SubsequentMessage paramSubsequentMessage) {
    this.tagNo = 1;
    this.obj = (ASN1Encodable)paramSubsequentMessage;
  }
  
  public int getType() {
    return this.tagNo;
  }
  
  public ASN1Encodable getValue() {
    return this.obj;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERTaggedObject(false, this.tagNo, this.obj);
  }
}
