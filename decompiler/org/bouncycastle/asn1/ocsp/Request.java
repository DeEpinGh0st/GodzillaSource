package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;

public class Request extends ASN1Object {
  CertID reqCert;
  
  Extensions singleRequestExtensions;
  
  public Request(CertID paramCertID, Extensions paramExtensions) {
    this.reqCert = paramCertID;
    this.singleRequestExtensions = paramExtensions;
  }
  
  private Request(ASN1Sequence paramASN1Sequence) {
    this.reqCert = CertID.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() == 2)
      this.singleRequestExtensions = Extensions.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1), true); 
  }
  
  public static Request getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static Request getInstance(Object paramObject) {
    return (paramObject instanceof Request) ? (Request)paramObject : ((paramObject != null) ? new Request(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public CertID getReqCert() {
    return this.reqCert;
  }
  
  public Extensions getSingleRequestExtensions() {
    return this.singleRequestExtensions;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.reqCert);
    if (this.singleRequestExtensions != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.singleRequestExtensions)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
