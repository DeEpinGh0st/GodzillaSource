package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class GOST3410PublicKeyAlgParameters extends ASN1Object {
  private ASN1ObjectIdentifier publicKeyParamSet;
  
  private ASN1ObjectIdentifier digestParamSet;
  
  private ASN1ObjectIdentifier encryptionParamSet;
  
  public static GOST3410PublicKeyAlgParameters getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static GOST3410PublicKeyAlgParameters getInstance(Object paramObject) {
    return (paramObject instanceof GOST3410PublicKeyAlgParameters) ? (GOST3410PublicKeyAlgParameters)paramObject : ((paramObject != null) ? new GOST3410PublicKeyAlgParameters(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public GOST3410PublicKeyAlgParameters(ASN1ObjectIdentifier paramASN1ObjectIdentifier1, ASN1ObjectIdentifier paramASN1ObjectIdentifier2) {
    this.publicKeyParamSet = paramASN1ObjectIdentifier1;
    this.digestParamSet = paramASN1ObjectIdentifier2;
    this.encryptionParamSet = null;
  }
  
  public GOST3410PublicKeyAlgParameters(ASN1ObjectIdentifier paramASN1ObjectIdentifier1, ASN1ObjectIdentifier paramASN1ObjectIdentifier2, ASN1ObjectIdentifier paramASN1ObjectIdentifier3) {
    this.publicKeyParamSet = paramASN1ObjectIdentifier1;
    this.digestParamSet = paramASN1ObjectIdentifier2;
    this.encryptionParamSet = paramASN1ObjectIdentifier3;
  }
  
  public GOST3410PublicKeyAlgParameters(ASN1Sequence paramASN1Sequence) {
    this.publicKeyParamSet = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(0);
    this.digestParamSet = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(1);
    if (paramASN1Sequence.size() > 2)
      this.encryptionParamSet = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(2); 
  }
  
  public ASN1ObjectIdentifier getPublicKeyParamSet() {
    return this.publicKeyParamSet;
  }
  
  public ASN1ObjectIdentifier getDigestParamSet() {
    return this.digestParamSet;
  }
  
  public ASN1ObjectIdentifier getEncryptionParamSet() {
    return this.encryptionParamSet;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.publicKeyParamSet);
    aSN1EncodableVector.add((ASN1Encodable)this.digestParamSet);
    if (this.encryptionParamSet != null)
      aSN1EncodableVector.add((ASN1Encodable)this.encryptionParamSet); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
