package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class EncryptedContentInfo extends ASN1Object {
  private ASN1ObjectIdentifier contentType;
  
  private AlgorithmIdentifier contentEncryptionAlgorithm;
  
  private ASN1OctetString encryptedContent;
  
  public EncryptedContentInfo(ASN1ObjectIdentifier paramASN1ObjectIdentifier, AlgorithmIdentifier paramAlgorithmIdentifier, ASN1OctetString paramASN1OctetString) {
    this.contentType = paramASN1ObjectIdentifier;
    this.contentEncryptionAlgorithm = paramAlgorithmIdentifier;
    this.encryptedContent = paramASN1OctetString;
  }
  
  private EncryptedContentInfo(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 2)
      throw new IllegalArgumentException("Truncated Sequence Found"); 
    this.contentType = (ASN1ObjectIdentifier)paramASN1Sequence.getObjectAt(0);
    this.contentEncryptionAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    if (paramASN1Sequence.size() > 2)
      this.encryptedContent = ASN1OctetString.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(2), false); 
  }
  
  public static EncryptedContentInfo getInstance(Object paramObject) {
    return (paramObject instanceof EncryptedContentInfo) ? (EncryptedContentInfo)paramObject : ((paramObject != null) ? new EncryptedContentInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1ObjectIdentifier getContentType() {
    return this.contentType;
  }
  
  public AlgorithmIdentifier getContentEncryptionAlgorithm() {
    return this.contentEncryptionAlgorithm;
  }
  
  public ASN1OctetString getEncryptedContent() {
    return this.encryptedContent;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.contentType);
    aSN1EncodableVector.add((ASN1Encodable)this.contentEncryptionAlgorithm);
    if (this.encryptedContent != null)
      aSN1EncodableVector.add((ASN1Encodable)new BERTaggedObject(false, 0, (ASN1Encodable)this.encryptedContent)); 
    return (ASN1Primitive)new BERSequence(aSN1EncodableVector);
  }
}
