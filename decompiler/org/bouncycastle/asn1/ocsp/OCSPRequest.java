package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class OCSPRequest extends ASN1Object {
  TBSRequest tbsRequest;
  
  Signature optionalSignature;
  
  public OCSPRequest(TBSRequest paramTBSRequest, Signature paramSignature) {
    this.tbsRequest = paramTBSRequest;
    this.optionalSignature = paramSignature;
  }
  
  private OCSPRequest(ASN1Sequence paramASN1Sequence) {
    this.tbsRequest = TBSRequest.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() == 2)
      this.optionalSignature = Signature.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1), true); 
  }
  
  public static OCSPRequest getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static OCSPRequest getInstance(Object paramObject) {
    return (paramObject instanceof OCSPRequest) ? (OCSPRequest)paramObject : ((paramObject != null) ? new OCSPRequest(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public TBSRequest getTbsRequest() {
    return this.tbsRequest;
  }
  
  public Signature getOptionalSignature() {
    return this.optionalSignature;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.tbsRequest);
    if (this.optionalSignature != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.optionalSignature)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
