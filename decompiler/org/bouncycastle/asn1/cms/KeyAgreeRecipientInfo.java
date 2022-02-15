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

public class KeyAgreeRecipientInfo extends ASN1Object {
  private ASN1Integer version;
  
  private OriginatorIdentifierOrKey originator;
  
  private ASN1OctetString ukm;
  
  private AlgorithmIdentifier keyEncryptionAlgorithm;
  
  private ASN1Sequence recipientEncryptedKeys;
  
  public KeyAgreeRecipientInfo(OriginatorIdentifierOrKey paramOriginatorIdentifierOrKey, ASN1OctetString paramASN1OctetString, AlgorithmIdentifier paramAlgorithmIdentifier, ASN1Sequence paramASN1Sequence) {
    this.version = new ASN1Integer(3L);
    this.originator = paramOriginatorIdentifierOrKey;
    this.ukm = paramASN1OctetString;
    this.keyEncryptionAlgorithm = paramAlgorithmIdentifier;
    this.recipientEncryptedKeys = paramASN1Sequence;
  }
  
  public KeyAgreeRecipientInfo(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    this.version = (ASN1Integer)paramASN1Sequence.getObjectAt(b++);
    this.originator = OriginatorIdentifierOrKey.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(b++), true);
    if (paramASN1Sequence.getObjectAt(b) instanceof ASN1TaggedObject)
      this.ukm = ASN1OctetString.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(b++), true); 
    this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(b++));
    this.recipientEncryptedKeys = (ASN1Sequence)paramASN1Sequence.getObjectAt(b++);
  }
  
  public static KeyAgreeRecipientInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static KeyAgreeRecipientInfo getInstance(Object paramObject) {
    return (paramObject instanceof KeyAgreeRecipientInfo) ? (KeyAgreeRecipientInfo)paramObject : ((paramObject != null) ? new KeyAgreeRecipientInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public OriginatorIdentifierOrKey getOriginator() {
    return this.originator;
  }
  
  public ASN1OctetString getUserKeyingMaterial() {
    return this.ukm;
  }
  
  public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
    return this.keyEncryptionAlgorithm;
  }
  
  public ASN1Sequence getRecipientEncryptedKeys() {
    return this.recipientEncryptedKeys;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.originator));
    if (this.ukm != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.ukm)); 
    aSN1EncodableVector.add((ASN1Encodable)this.keyEncryptionAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.recipientEncryptedKeys);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
