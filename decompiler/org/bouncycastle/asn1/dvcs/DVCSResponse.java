package org.bouncycastle.asn1.dvcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class DVCSResponse extends ASN1Object implements ASN1Choice {
  private DVCSCertInfo dvCertInfo;
  
  private DVCSErrorNotice dvErrorNote;
  
  public DVCSResponse(DVCSCertInfo paramDVCSCertInfo) {
    this.dvCertInfo = paramDVCSCertInfo;
  }
  
  public DVCSResponse(DVCSErrorNotice paramDVCSErrorNotice) {
    this.dvErrorNote = paramDVCSErrorNotice;
  }
  
  public static DVCSResponse getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DVCSResponse)
      return (DVCSResponse)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return getInstance(ASN1Primitive.fromByteArray((byte[])paramObject));
      } catch (IOException iOException) {
        throw new IllegalArgumentException("failed to construct sequence from byte[]: " + iOException.getMessage());
      }  
    if (paramObject instanceof ASN1Sequence) {
      DVCSCertInfo dVCSCertInfo = DVCSCertInfo.getInstance(paramObject);
      return new DVCSResponse(dVCSCertInfo);
    } 
    if (paramObject instanceof ASN1TaggedObject) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(paramObject);
      DVCSErrorNotice dVCSErrorNotice = DVCSErrorNotice.getInstance(aSN1TaggedObject, false);
      return new DVCSResponse(dVCSErrorNotice);
    } 
    throw new IllegalArgumentException("Couldn't convert from object to DVCSResponse: " + paramObject.getClass().getName());
  }
  
  public static DVCSResponse getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public DVCSCertInfo getCertInfo() {
    return this.dvCertInfo;
  }
  
  public DVCSErrorNotice getErrorNotice() {
    return this.dvErrorNote;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.dvCertInfo != null) ? this.dvCertInfo.toASN1Primitive() : new DERTaggedObject(false, 0, (ASN1Encodable)this.dvErrorNote));
  }
  
  public String toString() {
    return (this.dvCertInfo != null) ? ("DVCSResponse {\ndvCertInfo: " + this.dvCertInfo.toString() + "}\n") : ("DVCSResponse {\ndvErrorNote: " + this.dvErrorNote.toString() + "}\n");
  }
}
