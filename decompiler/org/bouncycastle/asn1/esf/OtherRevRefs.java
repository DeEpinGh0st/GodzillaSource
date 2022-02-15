package org.bouncycastle.asn1.esf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OtherRevRefs extends ASN1Object {
  private ASN1ObjectIdentifier otherRevRefType;
  
  private ASN1Encodable otherRevRefs;
  
  public static OtherRevRefs getInstance(Object paramObject) {
    return (paramObject instanceof OtherRevRefs) ? (OtherRevRefs)paramObject : ((paramObject != null) ? new OtherRevRefs(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private OtherRevRefs(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.otherRevRefType = new ASN1ObjectIdentifier(((ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(0)).getId());
    try {
      this.otherRevRefs = (ASN1Encodable)ASN1Primitive.fromByteArray(paramASN1Sequence.getObjectAt(1).toASN1Primitive().getEncoded("DER"));
    } catch (IOException iOException) {
      throw new IllegalStateException();
    } 
  }
  
  public OtherRevRefs(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.otherRevRefType = paramASN1ObjectIdentifier;
    this.otherRevRefs = paramASN1Encodable;
  }
  
  public ASN1ObjectIdentifier getOtherRevRefType() {
    return this.otherRevRefType;
  }
  
  public ASN1Encodable getOtherRevRefs() {
    return this.otherRevRefs;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.otherRevRefType);
    aSN1EncodableVector.add(this.otherRevRefs);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
