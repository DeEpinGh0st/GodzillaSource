package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;

public class ProofOfPossession extends ASN1Object implements ASN1Choice {
  public static final int TYPE_RA_VERIFIED = 0;
  
  public static final int TYPE_SIGNING_KEY = 1;
  
  public static final int TYPE_KEY_ENCIPHERMENT = 2;
  
  public static final int TYPE_KEY_AGREEMENT = 3;
  
  private int tagNo;
  
  private ASN1Encodable obj;
  
  private ProofOfPossession(ASN1TaggedObject paramASN1TaggedObject) {
    this.tagNo = paramASN1TaggedObject.getTagNo();
    switch (this.tagNo) {
      case 0:
        this.obj = (ASN1Encodable)DERNull.INSTANCE;
        return;
      case 1:
        this.obj = (ASN1Encodable)POPOSigningKey.getInstance(paramASN1TaggedObject, false);
        return;
      case 2:
      case 3:
        this.obj = (ASN1Encodable)POPOPrivKey.getInstance(paramASN1TaggedObject, true);
        return;
    } 
    throw new IllegalArgumentException("unknown tag: " + this.tagNo);
  }
  
  public static ProofOfPossession getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ProofOfPossession)
      return (ProofOfPossession)paramObject; 
    if (paramObject instanceof ASN1TaggedObject)
      return new ProofOfPossession((ASN1TaggedObject)paramObject); 
    throw new IllegalArgumentException("Invalid object: " + paramObject.getClass().getName());
  }
  
  public ProofOfPossession() {
    this.tagNo = 0;
    this.obj = (ASN1Encodable)DERNull.INSTANCE;
  }
  
  public ProofOfPossession(POPOSigningKey paramPOPOSigningKey) {
    this.tagNo = 1;
    this.obj = (ASN1Encodable)paramPOPOSigningKey;
  }
  
  public ProofOfPossession(int paramInt, POPOPrivKey paramPOPOPrivKey) {
    this.tagNo = paramInt;
    this.obj = (ASN1Encodable)paramPOPOPrivKey;
  }
  
  public int getType() {
    return this.tagNo;
  }
  
  public ASN1Encodable getObject() {
    return this.obj;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERTaggedObject(false, this.tagNo, this.obj);
  }
}
