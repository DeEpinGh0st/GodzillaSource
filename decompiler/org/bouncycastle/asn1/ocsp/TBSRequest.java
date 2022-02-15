package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509Extensions;

public class TBSRequest extends ASN1Object {
  private static final ASN1Integer V1 = new ASN1Integer(0L);
  
  ASN1Integer version;
  
  GeneralName requestorName;
  
  ASN1Sequence requestList;
  
  Extensions requestExtensions;
  
  boolean versionSet;
  
  public TBSRequest(GeneralName paramGeneralName, ASN1Sequence paramASN1Sequence, X509Extensions paramX509Extensions) {
    this.version = V1;
    this.requestorName = paramGeneralName;
    this.requestList = paramASN1Sequence;
    this.requestExtensions = Extensions.getInstance(paramX509Extensions);
  }
  
  public TBSRequest(GeneralName paramGeneralName, ASN1Sequence paramASN1Sequence, Extensions paramExtensions) {
    this.version = V1;
    this.requestorName = paramGeneralName;
    this.requestList = paramASN1Sequence;
    this.requestExtensions = paramExtensions;
  }
  
  private TBSRequest(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    if (paramASN1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramASN1Sequence.getObjectAt(0);
      if (aSN1TaggedObject.getTagNo() == 0) {
        this.versionSet = true;
        this.version = ASN1Integer.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(0), true);
        b++;
      } else {
        this.version = V1;
      } 
    } else {
      this.version = V1;
    } 
    if (paramASN1Sequence.getObjectAt(b) instanceof ASN1TaggedObject)
      this.requestorName = GeneralName.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(b++), true); 
    this.requestList = (ASN1Sequence)paramASN1Sequence.getObjectAt(b++);
    if (paramASN1Sequence.size() == b + 1)
      this.requestExtensions = Extensions.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(b), true); 
  }
  
  public static TBSRequest getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static TBSRequest getInstance(Object paramObject) {
    return (paramObject instanceof TBSRequest) ? (TBSRequest)paramObject : ((paramObject != null) ? new TBSRequest(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public GeneralName getRequestorName() {
    return this.requestorName;
  }
  
  public ASN1Sequence getRequestList() {
    return this.requestList;
  }
  
  public Extensions getRequestExtensions() {
    return this.requestExtensions;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (!this.version.equals(V1) || this.versionSet)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.version)); 
    if (this.requestorName != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.requestorName)); 
    aSN1EncodableVector.add((ASN1Encodable)this.requestList);
    if (this.requestExtensions != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)this.requestExtensions)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
