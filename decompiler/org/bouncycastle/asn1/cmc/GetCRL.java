package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.ReasonFlags;

public class GetCRL extends ASN1Object {
  private final X500Name issuerName;
  
  private GeneralName cRLName;
  
  private ASN1GeneralizedTime time;
  
  private ReasonFlags reasons;
  
  public GetCRL(X500Name paramX500Name, GeneralName paramGeneralName, ASN1GeneralizedTime paramASN1GeneralizedTime, ReasonFlags paramReasonFlags) {
    this.issuerName = paramX500Name;
    this.cRLName = paramGeneralName;
    this.time = paramASN1GeneralizedTime;
    this.reasons = paramReasonFlags;
  }
  
  private GetCRL(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 1 || paramASN1Sequence.size() > 4)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.issuerName = X500Name.getInstance(paramASN1Sequence.getObjectAt(0));
    byte b = 1;
    if (paramASN1Sequence.size() > b && paramASN1Sequence.getObjectAt(b).toASN1Primitive() instanceof org.bouncycastle.asn1.ASN1TaggedObject)
      this.cRLName = GeneralName.getInstance(paramASN1Sequence.getObjectAt(b++)); 
    if (paramASN1Sequence.size() > b && paramASN1Sequence.getObjectAt(b).toASN1Primitive() instanceof ASN1GeneralizedTime)
      this.time = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(b++)); 
    if (paramASN1Sequence.size() > b && paramASN1Sequence.getObjectAt(b).toASN1Primitive() instanceof DERBitString)
      this.reasons = new ReasonFlags(DERBitString.getInstance(paramASN1Sequence.getObjectAt(b))); 
  }
  
  public static GetCRL getInstance(Object paramObject) {
    return (paramObject instanceof GetCRL) ? (GetCRL)paramObject : ((paramObject != null) ? new GetCRL(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public X500Name getIssuerName() {
    return this.issuerName;
  }
  
  public GeneralName getcRLName() {
    return this.cRLName;
  }
  
  public ASN1GeneralizedTime getTime() {
    return this.time;
  }
  
  public ReasonFlags getReasons() {
    return this.reasons;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.issuerName);
    if (this.cRLName != null)
      aSN1EncodableVector.add((ASN1Encodable)this.cRLName); 
    if (this.time != null)
      aSN1EncodableVector.add((ASN1Encodable)this.time); 
    if (this.reasons != null)
      aSN1EncodableVector.add((ASN1Encodable)this.reasons); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
