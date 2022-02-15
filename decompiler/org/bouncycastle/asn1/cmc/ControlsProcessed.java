package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ControlsProcessed extends ASN1Object {
  private final ASN1Sequence bodyPartReferences;
  
  public ControlsProcessed(BodyPartReference paramBodyPartReference) {
    this.bodyPartReferences = (ASN1Sequence)new DERSequence((ASN1Encodable)paramBodyPartReference);
  }
  
  public ControlsProcessed(BodyPartReference[] paramArrayOfBodyPartReference) {
    this.bodyPartReferences = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfBodyPartReference);
  }
  
  public static ControlsProcessed getInstance(Object paramObject) {
    return (paramObject instanceof ControlsProcessed) ? (ControlsProcessed)paramObject : ((paramObject != null) ? new ControlsProcessed(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private ControlsProcessed(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 1)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.bodyPartReferences = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(0));
  }
  
  public BodyPartReference[] getBodyList() {
    BodyPartReference[] arrayOfBodyPartReference = new BodyPartReference[this.bodyPartReferences.size()];
    for (byte b = 0; b != this.bodyPartReferences.size(); b++)
      arrayOfBodyPartReference[b] = BodyPartReference.getInstance(this.bodyPartReferences.getObjectAt(b)); 
    return arrayOfBodyPartReference;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence((ASN1Encodable)this.bodyPartReferences);
  }
}
