package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.DLTaggedObject;

public class SafeBag extends ASN1Object {
  private ASN1ObjectIdentifier bagId;
  
  private ASN1Encodable bagValue;
  
  private ASN1Set bagAttributes;
  
  public SafeBag(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.bagId = paramASN1ObjectIdentifier;
    this.bagValue = paramASN1Encodable;
    this.bagAttributes = null;
  }
  
  public SafeBag(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable, ASN1Set paramASN1Set) {
    this.bagId = paramASN1ObjectIdentifier;
    this.bagValue = paramASN1Encodable;
    this.bagAttributes = paramASN1Set;
  }
  
  public static SafeBag getInstance(Object paramObject) {
    return (paramObject instanceof SafeBag) ? (SafeBag)paramObject : ((paramObject != null) ? new SafeBag(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private SafeBag(ASN1Sequence paramASN1Sequence) {
    this.bagId = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(0);
    this.bagValue = (ASN1Encodable)((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1)).getObject();
    if (paramASN1Sequence.size() == 3)
      this.bagAttributes = (ASN1Set)paramASN1Sequence.getObjectAt(2); 
  }
  
  public ASN1ObjectIdentifier getBagId() {
    return this.bagId;
  }
  
  public ASN1Encodable getBagValue() {
    return this.bagValue;
  }
  
  public ASN1Set getBagAttributes() {
    return this.bagAttributes;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.bagId);
    aSN1EncodableVector.add((ASN1Encodable)new DLTaggedObject(true, 0, this.bagValue));
    if (this.bagAttributes != null)
      aSN1EncodableVector.add((ASN1Encodable)this.bagAttributes); 
    return (ASN1Primitive)new DLSequence(aSN1EncodableVector);
  }
}
