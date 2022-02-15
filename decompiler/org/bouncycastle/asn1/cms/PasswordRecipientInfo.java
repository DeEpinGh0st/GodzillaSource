package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PasswordRecipientInfo extends ASN1Object {
  private ASN1Integer version = new ASN1Integer(0L);
  
  private AlgorithmIdentifier keyDerivationAlgorithm;
  
  private AlgorithmIdentifier keyEncryptionAlgorithm;
  
  private ASN1OctetString encryptedKey;
  
  public PasswordRecipientInfo(AlgorithmIdentifier paramAlgorithmIdentifier, ASN1OctetString paramASN1OctetString) {
    this.keyEncryptionAlgorithm = paramAlgorithmIdentifier;
    this.encryptedKey = paramASN1OctetString;
  }
  
  public PasswordRecipientInfo(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, ASN1OctetString paramASN1OctetString) {
    this.keyDerivationAlgorithm = paramAlgorithmIdentifier1;
    this.keyEncryptionAlgorithm = paramAlgorithmIdentifier2;
    this.encryptedKey = paramASN1OctetString;
  }
  
  public PasswordRecipientInfo(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.getObjectAt(1) instanceof ASN1TaggedObject) {
      this.keyDerivationAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1), false);
      this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(2));
      this.encryptedKey = (ASN1OctetString)paramASN1Sequence.getObjectAt(3);
    } else {
      this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
      this.encryptedKey = (ASN1OctetString)paramASN1Sequence.getObjectAt(2);
    } 
  }
  
  public static PasswordRecipientInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static PasswordRecipientInfo getInstance(Object paramObject) {
    return (paramObject instanceof PasswordRecipientInfo) ? (PasswordRecipientInfo)paramObject : ((paramObject != null) ? new PasswordRecipientInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public AlgorithmIdentifier getKeyDerivationAlgorithm() {
    return this.keyDerivationAlgorithm;
  }
  
  public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
    return this.keyEncryptionAlgorithm;
  }
  
  public ASN1OctetString getEncryptedKey() {
    return this.encryptedKey;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    if (this.keyDerivationAlgorithm != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.keyDerivationAlgorithm)); 
    aSN1EncodableVector.add((ASN1Encodable)this.keyEncryptionAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.encryptedKey);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
