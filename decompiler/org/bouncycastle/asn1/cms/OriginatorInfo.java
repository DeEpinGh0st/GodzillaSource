package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class OriginatorInfo extends ASN1Object {
  private ASN1Set certs;
  
  private ASN1Set crls;
  
  public OriginatorInfo(ASN1Set paramASN1Set1, ASN1Set paramASN1Set2) {
    this.certs = paramASN1Set1;
    this.crls = paramASN1Set2;
  }
  
  private OriginatorInfo(ASN1Sequence paramASN1Sequence) {
    ASN1TaggedObject aSN1TaggedObject;
    switch (paramASN1Sequence.size()) {
      case 0:
        return;
      case 1:
        aSN1TaggedObject = (ASN1TaggedObject)paramASN1Sequence.getObjectAt(0);
        switch (aSN1TaggedObject.getTagNo()) {
          case 0:
            this.certs = ASN1Set.getInstance(aSN1TaggedObject, false);
          case 1:
            this.crls = ASN1Set.getInstance(aSN1TaggedObject, false);
        } 
        throw new IllegalArgumentException("Bad tag in OriginatorInfo: " + aSN1TaggedObject.getTagNo());
      case 2:
        this.certs = ASN1Set.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(0), false);
        this.crls = ASN1Set.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1), false);
    } 
    throw new IllegalArgumentException("OriginatorInfo too big");
  }
  
  public static OriginatorInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static OriginatorInfo getInstance(Object paramObject) {
    return (paramObject instanceof OriginatorInfo) ? (OriginatorInfo)paramObject : ((paramObject != null) ? new OriginatorInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Set getCertificates() {
    return this.certs;
  }
  
  public ASN1Set getCRLs() {
    return this.crls;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.certs != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.certs)); 
    if (this.crls != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.crls)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
