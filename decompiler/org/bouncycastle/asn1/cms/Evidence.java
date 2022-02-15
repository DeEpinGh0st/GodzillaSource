package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class Evidence extends ASN1Object implements ASN1Choice {
  private TimeStampTokenEvidence tstEvidence;
  
  public Evidence(TimeStampTokenEvidence paramTimeStampTokenEvidence) {
    this.tstEvidence = paramTimeStampTokenEvidence;
  }
  
  private Evidence(ASN1TaggedObject paramASN1TaggedObject) {
    if (paramASN1TaggedObject.getTagNo() == 0)
      this.tstEvidence = TimeStampTokenEvidence.getInstance(paramASN1TaggedObject, false); 
  }
  
  public static Evidence getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof Evidence)
      return (Evidence)paramObject; 
    if (paramObject instanceof ASN1TaggedObject)
      return new Evidence(ASN1TaggedObject.getInstance(paramObject)); 
    throw new IllegalArgumentException("unknown object in getInstance");
  }
  
  public TimeStampTokenEvidence getTstEvidence() {
    return this.tstEvidence;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.tstEvidence != null) ? new DERTaggedObject(false, 0, (ASN1Encodable)this.tstEvidence) : null);
  }
}
