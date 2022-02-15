package org.bouncycastle.asn1.cms.ecc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;

public class MQVuserKeyingMaterial extends ASN1Object {
  private OriginatorPublicKey ephemeralPublicKey;
  
  private ASN1OctetString addedukm;
  
  public MQVuserKeyingMaterial(OriginatorPublicKey paramOriginatorPublicKey, ASN1OctetString paramASN1OctetString) {
    if (paramOriginatorPublicKey == null)
      throw new IllegalArgumentException("Ephemeral public key cannot be null"); 
    this.ephemeralPublicKey = paramOriginatorPublicKey;
    this.addedukm = paramASN1OctetString;
  }
  
  private MQVuserKeyingMaterial(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 1 && paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Sequence has incorrect number of elements"); 
    this.ephemeralPublicKey = OriginatorPublicKey.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() > 1)
      this.addedukm = ASN1OctetString.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1), true); 
  }
  
  public static MQVuserKeyingMaterial getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static MQVuserKeyingMaterial getInstance(Object paramObject) {
    return (paramObject instanceof MQVuserKeyingMaterial) ? (MQVuserKeyingMaterial)paramObject : ((paramObject != null) ? new MQVuserKeyingMaterial(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public OriginatorPublicKey getEphemeralPublicKey() {
    return this.ephemeralPublicKey;
  }
  
  public ASN1OctetString getAddedukm() {
    return this.addedukm;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.ephemeralPublicKey);
    if (this.addedukm != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.addedukm)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
