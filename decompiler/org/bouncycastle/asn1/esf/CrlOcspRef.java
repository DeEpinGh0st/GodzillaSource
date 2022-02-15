package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class CrlOcspRef extends ASN1Object {
  private CrlListID crlids;
  
  private OcspListID ocspids;
  
  private OtherRevRefs otherRev;
  
  public static CrlOcspRef getInstance(Object paramObject) {
    return (paramObject instanceof CrlOcspRef) ? (CrlOcspRef)paramObject : ((paramObject != null) ? new CrlOcspRef(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private CrlOcspRef(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1TaggedObject> enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = enumeration.nextElement();
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.crlids = CrlListID.getInstance(aSN1TaggedObject.getObject());
          continue;
        case 1:
          this.ocspids = OcspListID.getInstance(aSN1TaggedObject.getObject());
          continue;
        case 2:
          this.otherRev = OtherRevRefs.getInstance(aSN1TaggedObject.getObject());
          continue;
      } 
      throw new IllegalArgumentException("illegal tag");
    } 
  }
  
  public CrlOcspRef(CrlListID paramCrlListID, OcspListID paramOcspListID, OtherRevRefs paramOtherRevRefs) {
    this.crlids = paramCrlListID;
    this.ocspids = paramOcspListID;
    this.otherRev = paramOtherRevRefs;
  }
  
  public CrlListID getCrlids() {
    return this.crlids;
  }
  
  public OcspListID getOcspids() {
    return this.ocspids;
  }
  
  public OtherRevRefs getOtherRev() {
    return this.otherRev;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (null != this.crlids)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.crlids.toASN1Primitive())); 
    if (null != this.ocspids)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.ocspids.toASN1Primitive())); 
    if (null != this.otherRev)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)this.otherRev.toASN1Primitive())); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
