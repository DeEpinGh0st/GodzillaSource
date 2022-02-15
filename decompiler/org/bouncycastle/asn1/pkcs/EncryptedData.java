package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class EncryptedData extends ASN1Object {
  ASN1Sequence data;
  
  ASN1ObjectIdentifier bagId;
  
  ASN1Primitive bagValue;
  
  public static EncryptedData getInstance(Object paramObject) {
    return (paramObject instanceof EncryptedData) ? (EncryptedData)paramObject : ((paramObject != null) ? new EncryptedData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private EncryptedData(ASN1Sequence paramASN1Sequence) {
    int i = ((ASN1Integer)paramASN1Sequence.getObjectAt(0)).getValue().intValue();
    if (i != 0)
      throw new IllegalArgumentException("sequence not version 0"); 
    this.data = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public EncryptedData(ASN1ObjectIdentifier paramASN1ObjectIdentifier, AlgorithmIdentifier paramAlgorithmIdentifier, ASN1Encodable paramASN1Encodable) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)paramASN1ObjectIdentifier);
    aSN1EncodableVector.add((ASN1Encodable)paramAlgorithmIdentifier.toASN1Primitive());
    aSN1EncodableVector.add((ASN1Encodable)new BERTaggedObject(false, 0, paramASN1Encodable));
    this.data = (ASN1Sequence)new BERSequence(aSN1EncodableVector);
  }
  
  public ASN1ObjectIdentifier getContentType() {
    return ASN1ObjectIdentifier.getInstance(this.data.getObjectAt(0));
  }
  
  public AlgorithmIdentifier getEncryptionAlgorithm() {
    return AlgorithmIdentifier.getInstance(this.data.getObjectAt(1));
  }
  
  public ASN1OctetString getContent() {
    if (this.data.size() == 3) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(this.data.getObjectAt(2));
      return ASN1OctetString.getInstance(aSN1TaggedObject, false);
    } 
    return null;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(0L));
    aSN1EncodableVector.add((ASN1Encodable)this.data);
    return (ASN1Primitive)new BERSequence(aSN1EncodableVector);
  }
}
