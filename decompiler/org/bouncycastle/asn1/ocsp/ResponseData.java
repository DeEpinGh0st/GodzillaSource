package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.X509Extensions;

public class ResponseData extends ASN1Object {
  private static final ASN1Integer V1 = new ASN1Integer(0L);
  
  private boolean versionPresent;
  
  private ASN1Integer version;
  
  private ResponderID responderID;
  
  private ASN1GeneralizedTime producedAt;
  
  private ASN1Sequence responses;
  
  private Extensions responseExtensions;
  
  public ResponseData(ASN1Integer paramASN1Integer, ResponderID paramResponderID, ASN1GeneralizedTime paramASN1GeneralizedTime, ASN1Sequence paramASN1Sequence, Extensions paramExtensions) {
    this.version = paramASN1Integer;
    this.responderID = paramResponderID;
    this.producedAt = paramASN1GeneralizedTime;
    this.responses = paramASN1Sequence;
    this.responseExtensions = paramExtensions;
  }
  
  public ResponseData(ResponderID paramResponderID, ASN1GeneralizedTime paramASN1GeneralizedTime, ASN1Sequence paramASN1Sequence, X509Extensions paramX509Extensions) {
    this(V1, paramResponderID, ASN1GeneralizedTime.getInstance(paramASN1GeneralizedTime), paramASN1Sequence, Extensions.getInstance(paramX509Extensions));
  }
  
  public ResponseData(ResponderID paramResponderID, ASN1GeneralizedTime paramASN1GeneralizedTime, ASN1Sequence paramASN1Sequence, Extensions paramExtensions) {
    this(V1, paramResponderID, paramASN1GeneralizedTime, paramASN1Sequence, paramExtensions);
  }
  
  private ResponseData(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    if (paramASN1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramASN1Sequence.getObjectAt(0);
      if (aSN1TaggedObject.getTagNo() == 0) {
        this.versionPresent = true;
        this.version = ASN1Integer.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(0), true);
        b++;
      } else {
        this.version = V1;
      } 
    } else {
      this.version = V1;
    } 
    this.responderID = ResponderID.getInstance(paramASN1Sequence.getObjectAt(b++));
    this.producedAt = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(b++));
    this.responses = (ASN1Sequence)paramASN1Sequence.getObjectAt(b++);
    if (paramASN1Sequence.size() > b)
      this.responseExtensions = Extensions.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(b), true); 
  }
  
  public static ResponseData getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static ResponseData getInstance(Object paramObject) {
    return (paramObject instanceof ResponseData) ? (ResponseData)paramObject : ((paramObject != null) ? new ResponseData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public ResponderID getResponderID() {
    return this.responderID;
  }
  
  public ASN1GeneralizedTime getProducedAt() {
    return this.producedAt;
  }
  
  public ASN1Sequence getResponses() {
    return this.responses;
  }
  
  public Extensions getResponseExtensions() {
    return this.responseExtensions;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.versionPresent || !this.version.equals(V1))
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.version)); 
    aSN1EncodableVector.add((ASN1Encodable)this.responderID);
    aSN1EncodableVector.add((ASN1Encodable)this.producedAt);
    aSN1EncodableVector.add((ASN1Encodable)this.responses);
    if (this.responseExtensions != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.responseExtensions)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
