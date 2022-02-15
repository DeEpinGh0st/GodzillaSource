package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class CRLBag extends ASN1Object {
  private ASN1ObjectIdentifier crlId;
  
  private ASN1Encodable crlValue;
  
  private CRLBag(ASN1Sequence paramASN1Sequence) {
    this.crlId = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(0);
    this.crlValue = (ASN1Encodable)((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1)).getObject();
  }
  
  public static CRLBag getInstance(Object paramObject) {
    return (paramObject instanceof CRLBag) ? (CRLBag)paramObject : ((paramObject != null) ? new CRLBag(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public CRLBag(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.crlId = paramASN1ObjectIdentifier;
    this.crlValue = paramASN1Encodable;
  }
  
  public ASN1ObjectIdentifier getCrlId() {
    return this.crlId;
  }
  
  public ASN1Encodable getCrlValue() {
    return this.crlValue;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.crlId);
    aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(0, this.crlValue));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
