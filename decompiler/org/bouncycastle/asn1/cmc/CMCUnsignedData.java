package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CMCUnsignedData extends ASN1Object {
  private final BodyPartPath bodyPartPath;
  
  private final ASN1ObjectIdentifier identifier;
  
  private final ASN1Encodable content;
  
  public CMCUnsignedData(BodyPartPath paramBodyPartPath, ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.bodyPartPath = paramBodyPartPath;
    this.identifier = paramASN1ObjectIdentifier;
    this.content = paramASN1Encodable;
  }
  
  private CMCUnsignedData(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.bodyPartPath = BodyPartPath.getInstance(paramASN1Sequence.getObjectAt(0));
    this.identifier = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.content = paramASN1Sequence.getObjectAt(2);
  }
  
  public static CMCUnsignedData getInstance(Object paramObject) {
    return (paramObject instanceof CMCUnsignedData) ? (CMCUnsignedData)paramObject : ((paramObject != null) ? new CMCUnsignedData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.bodyPartPath);
    aSN1EncodableVector.add((ASN1Encodable)this.identifier);
    aSN1EncodableVector.add(this.content);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public BodyPartPath getBodyPartPath() {
    return this.bodyPartPath;
  }
  
  public ASN1ObjectIdentifier getIdentifier() {
    return this.identifier;
  }
  
  public ASN1Encodable getContent() {
    return this.content;
  }
}
