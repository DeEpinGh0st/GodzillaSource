package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class OtherRevocationInfoFormat extends ASN1Object {
  private ASN1ObjectIdentifier otherRevInfoFormat;
  
  private ASN1Encodable otherRevInfo;
  
  public OtherRevocationInfoFormat(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.otherRevInfoFormat = paramASN1ObjectIdentifier;
    this.otherRevInfo = paramASN1Encodable;
  }
  
  private OtherRevocationInfoFormat(ASN1Sequence paramASN1Sequence) {
    this.otherRevInfoFormat = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.otherRevInfo = paramASN1Sequence.getObjectAt(1);
  }
  
  public static OtherRevocationInfoFormat getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static OtherRevocationInfoFormat getInstance(Object paramObject) {
    return (paramObject instanceof OtherRevocationInfoFormat) ? (OtherRevocationInfoFormat)paramObject : ((paramObject != null) ? new OtherRevocationInfoFormat(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1ObjectIdentifier getInfoFormat() {
    return this.otherRevInfoFormat;
  }
  
  public ASN1Encodable getInfo() {
    return this.otherRevInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.otherRevInfoFormat);
    aSN1EncodableVector.add(this.otherRevInfo);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
