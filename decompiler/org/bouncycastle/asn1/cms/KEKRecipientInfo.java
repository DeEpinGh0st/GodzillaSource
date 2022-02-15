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
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KEKRecipientInfo extends ASN1Object {
  private ASN1Integer version = new ASN1Integer(4L);
  
  private KEKIdentifier kekid;
  
  private AlgorithmIdentifier keyEncryptionAlgorithm;
  
  private ASN1OctetString encryptedKey;
  
  public KEKRecipientInfo(KEKIdentifier paramKEKIdentifier, AlgorithmIdentifier paramAlgorithmIdentifier, ASN1OctetString paramASN1OctetString) {
    this.kekid = paramKEKIdentifier;
    this.keyEncryptionAlgorithm = paramAlgorithmIdentifier;
    this.encryptedKey = paramASN1OctetString;
  }
  
  public KEKRecipientInfo(ASN1Sequence paramASN1Sequence) {
    this.kekid = KEKIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(2));
    this.encryptedKey = (ASN1OctetString)paramASN1Sequence.getObjectAt(3);
  }
  
  public static KEKRecipientInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static KEKRecipientInfo getInstance(Object paramObject) {
    return (paramObject instanceof KEKRecipientInfo) ? (KEKRecipientInfo)paramObject : ((paramObject != null) ? new KEKRecipientInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public KEKIdentifier getKekid() {
    return this.kekid;
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
    aSN1EncodableVector.add((ASN1Encodable)this.kekid);
    aSN1EncodableVector.add((ASN1Encodable)this.keyEncryptionAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.encryptedKey);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
