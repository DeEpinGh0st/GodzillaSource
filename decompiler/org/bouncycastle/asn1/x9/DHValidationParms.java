package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;

public class DHValidationParms extends ASN1Object {
  private DERBitString seed;
  
  private ASN1Integer pgenCounter;
  
  public static DHValidationParms getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static DHValidationParms getInstance(Object paramObject) {
    return (paramObject instanceof DHValidationParms) ? (DHValidationParms)paramObject : ((paramObject != null) ? new DHValidationParms(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public DHValidationParms(DERBitString paramDERBitString, ASN1Integer paramASN1Integer) {
    if (paramDERBitString == null)
      throw new IllegalArgumentException("'seed' cannot be null"); 
    if (paramASN1Integer == null)
      throw new IllegalArgumentException("'pgenCounter' cannot be null"); 
    this.seed = paramDERBitString;
    this.pgenCounter = paramASN1Integer;
  }
  
  private DHValidationParms(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.seed = DERBitString.getInstance(paramASN1Sequence.getObjectAt(0));
    this.pgenCounter = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public DERBitString getSeed() {
    return this.seed;
  }
  
  public ASN1Integer getPgenCounter() {
    return this.pgenCounter;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.seed);
    aSN1EncodableVector.add((ASN1Encodable)this.pgenCounter);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
