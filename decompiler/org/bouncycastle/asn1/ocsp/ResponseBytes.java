package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class ResponseBytes extends ASN1Object {
  ASN1ObjectIdentifier responseType;
  
  ASN1OctetString response;
  
  public ResponseBytes(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1OctetString paramASN1OctetString) {
    this.responseType = paramASN1ObjectIdentifier;
    this.response = paramASN1OctetString;
  }
  
  public ResponseBytes(ASN1Sequence paramASN1Sequence) {
    this.responseType = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(0);
    this.response = (ASN1OctetString)paramASN1Sequence.getObjectAt(1);
  }
  
  public static ResponseBytes getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static ResponseBytes getInstance(Object paramObject) {
    return (paramObject instanceof ResponseBytes) ? (ResponseBytes)paramObject : ((paramObject != null) ? new ResponseBytes(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1ObjectIdentifier getResponseType() {
    return this.responseType;
  }
  
  public ASN1OctetString getResponse() {
    return this.response;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.responseType);
    aSN1EncodableVector.add((ASN1Encodable)this.response);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
