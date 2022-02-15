package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class OCSPResponse extends ASN1Object {
  OCSPResponseStatus responseStatus;
  
  ResponseBytes responseBytes;
  
  public OCSPResponse(OCSPResponseStatus paramOCSPResponseStatus, ResponseBytes paramResponseBytes) {
    this.responseStatus = paramOCSPResponseStatus;
    this.responseBytes = paramResponseBytes;
  }
  
  private OCSPResponse(ASN1Sequence paramASN1Sequence) {
    this.responseStatus = OCSPResponseStatus.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() == 2)
      this.responseBytes = ResponseBytes.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1), true); 
  }
  
  public static OCSPResponse getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static OCSPResponse getInstance(Object paramObject) {
    return (paramObject instanceof OCSPResponse) ? (OCSPResponse)paramObject : ((paramObject != null) ? new OCSPResponse(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public OCSPResponseStatus getResponseStatus() {
    return this.responseStatus;
  }
  
  public ResponseBytes getResponseBytes() {
    return this.responseBytes;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.responseStatus);
    if (this.responseBytes != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.responseBytes)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
