package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class BodyPartPath extends ASN1Object {
  private final BodyPartID[] bodyPartIDs;
  
  public static BodyPartPath getInstance(Object paramObject) {
    return (paramObject instanceof BodyPartPath) ? (BodyPartPath)paramObject : ((paramObject != null) ? new BodyPartPath(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static BodyPartPath getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public BodyPartPath(BodyPartID paramBodyPartID) {
    this.bodyPartIDs = new BodyPartID[] { paramBodyPartID };
  }
  
  public BodyPartPath(BodyPartID[] paramArrayOfBodyPartID) {
    this.bodyPartIDs = Utils.clone(paramArrayOfBodyPartID);
  }
  
  private BodyPartPath(ASN1Sequence paramASN1Sequence) {
    this.bodyPartIDs = Utils.toBodyPartIDArray(paramASN1Sequence);
  }
  
  public BodyPartID[] getBodyPartIDs() {
    return Utils.clone(this.bodyPartIDs);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence((ASN1Encodable[])this.bodyPartIDs);
  }
}
