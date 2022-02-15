package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class BodyPartList extends ASN1Object {
  private final BodyPartID[] bodyPartIDs;
  
  public static BodyPartList getInstance(Object paramObject) {
    return (paramObject instanceof BodyPartList) ? (BodyPartList)paramObject : ((paramObject != null) ? new BodyPartList(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static BodyPartList getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public BodyPartList(BodyPartID paramBodyPartID) {
    this.bodyPartIDs = new BodyPartID[] { paramBodyPartID };
  }
  
  public BodyPartList(BodyPartID[] paramArrayOfBodyPartID) {
    this.bodyPartIDs = Utils.clone(paramArrayOfBodyPartID);
  }
  
  private BodyPartList(ASN1Sequence paramASN1Sequence) {
    this.bodyPartIDs = Utils.toBodyPartIDArray(paramASN1Sequence);
  }
  
  public BodyPartID[] getBodyPartIDs() {
    return Utils.clone(this.bodyPartIDs);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence((ASN1Encodable[])this.bodyPartIDs);
  }
}
