package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class LraPopWitness extends ASN1Object {
  private final BodyPartID pkiDataBodyid;
  
  private final ASN1Sequence bodyIds;
  
  public LraPopWitness(BodyPartID paramBodyPartID, ASN1Sequence paramASN1Sequence) {
    this.pkiDataBodyid = paramBodyPartID;
    this.bodyIds = paramASN1Sequence;
  }
  
  private LraPopWitness(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.pkiDataBodyid = BodyPartID.getInstance(paramASN1Sequence.getObjectAt(0));
    this.bodyIds = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static LraPopWitness getInstance(Object paramObject) {
    return (paramObject instanceof LraPopWitness) ? (LraPopWitness)paramObject : ((paramObject != null) ? new LraPopWitness(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public BodyPartID getPkiDataBodyid() {
    return this.pkiDataBodyid;
  }
  
  public BodyPartID[] getBodyIds() {
    BodyPartID[] arrayOfBodyPartID = new BodyPartID[this.bodyIds.size()];
    for (byte b = 0; b != this.bodyIds.size(); b++)
      arrayOfBodyPartID[b] = BodyPartID.getInstance(this.bodyIds.getObjectAt(b)); 
    return arrayOfBodyPartID;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.pkiDataBodyid);
    aSN1EncodableVector.add((ASN1Encodable)this.bodyIds);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
