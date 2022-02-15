package org.bouncycastle.asn1.esf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OtherRevVals extends ASN1Object {
  private ASN1ObjectIdentifier otherRevValType;
  
  private ASN1Encodable otherRevVals;
  
  public static OtherRevVals getInstance(Object paramObject) {
    return (paramObject instanceof OtherRevVals) ? (OtherRevVals)paramObject : ((paramObject != null) ? new OtherRevVals(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private OtherRevVals(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.otherRevValType = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(0);
    try {
      this.otherRevVals = (ASN1Encodable)ASN1Primitive.fromByteArray(paramASN1Sequence.getObjectAt(1).toASN1Primitive().getEncoded("DER"));
    } catch (IOException iOException) {
      throw new IllegalStateException();
    } 
  }
  
  public OtherRevVals(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.otherRevValType = paramASN1ObjectIdentifier;
    this.otherRevVals = paramASN1Encodable;
  }
  
  public ASN1ObjectIdentifier getOtherRevValType() {
    return this.otherRevValType;
  }
  
  public ASN1Encodable getOtherRevVals() {
    return this.otherRevVals;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.otherRevValType);
    aSN1EncodableVector.add(this.otherRevVals);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
