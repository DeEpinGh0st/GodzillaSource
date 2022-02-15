package org.bouncycastle.asn1.crmf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Time;

public class OptionalValidity extends ASN1Object {
  private Time notBefore;
  
  private Time notAfter;
  
  private OptionalValidity(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1TaggedObject> enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = enumeration.nextElement();
      if (aSN1TaggedObject.getTagNo() == 0) {
        this.notBefore = Time.getInstance(aSN1TaggedObject, true);
        continue;
      } 
      this.notAfter = Time.getInstance(aSN1TaggedObject, true);
    } 
  }
  
  public static OptionalValidity getInstance(Object paramObject) {
    return (paramObject instanceof OptionalValidity) ? (OptionalValidity)paramObject : ((paramObject != null) ? new OptionalValidity(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public OptionalValidity(Time paramTime1, Time paramTime2) {
    if (paramTime1 == null && paramTime2 == null)
      throw new IllegalArgumentException("at least one of notBefore/notAfter must not be null."); 
    this.notBefore = paramTime1;
    this.notAfter = paramTime2;
  }
  
  public Time getNotBefore() {
    return this.notBefore;
  }
  
  public Time getNotAfter() {
    return this.notAfter;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.notBefore != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.notBefore)); 
    if (this.notAfter != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.notAfter)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
