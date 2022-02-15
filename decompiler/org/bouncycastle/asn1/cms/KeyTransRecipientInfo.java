package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KeyTransRecipientInfo extends ASN1Object {
  private ASN1Integer version;
  
  private RecipientIdentifier rid;
  
  private AlgorithmIdentifier keyEncryptionAlgorithm;
  
  private ASN1OctetString encryptedKey;
  
  public KeyTransRecipientInfo(RecipientIdentifier paramRecipientIdentifier, AlgorithmIdentifier paramAlgorithmIdentifier, ASN1OctetString paramASN1OctetString) {
    if (paramRecipientIdentifier.toASN1Primitive() instanceof org.bouncycastle.asn1.ASN1TaggedObject) {
      this.version = new ASN1Integer(2L);
    } else {
      this.version = new ASN1Integer(0L);
    } 
    this.rid = paramRecipientIdentifier;
    this.keyEncryptionAlgorithm = paramAlgorithmIdentifier;
    this.encryptedKey = paramASN1OctetString;
  }
  
  public KeyTransRecipientInfo(ASN1Sequence paramASN1Sequence) {
    this.version = (ASN1Integer)paramASN1Sequence.getObjectAt(0);
    this.rid = RecipientIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(2));
    this.encryptedKey = (ASN1OctetString)paramASN1Sequence.getObjectAt(3);
  }
  
  public static KeyTransRecipientInfo getInstance(Object paramObject) {
    return (paramObject instanceof KeyTransRecipientInfo) ? (KeyTransRecipientInfo)paramObject : ((paramObject != null) ? new KeyTransRecipientInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public RecipientIdentifier getRecipientIdentifier() {
    return this.rid;
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
    aSN1EncodableVector.add((ASN1Encodable)this.rid);
    aSN1EncodableVector.add((ASN1Encodable)this.keyEncryptionAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.encryptedKey);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
