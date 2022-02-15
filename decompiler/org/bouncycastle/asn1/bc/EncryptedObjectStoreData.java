package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class EncryptedObjectStoreData extends ASN1Object {
  private final AlgorithmIdentifier encryptionAlgorithm;
  
  private final ASN1OctetString encryptedContent;
  
  public EncryptedObjectStoreData(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) {
    this.encryptionAlgorithm = paramAlgorithmIdentifier;
    this.encryptedContent = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
  }
  
  private EncryptedObjectStoreData(ASN1Sequence paramASN1Sequence) {
    this.encryptionAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.encryptedContent = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static EncryptedObjectStoreData getInstance(Object paramObject) {
    return (paramObject instanceof EncryptedObjectStoreData) ? (EncryptedObjectStoreData)paramObject : ((paramObject != null) ? new EncryptedObjectStoreData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1OctetString getEncryptedContent() {
    return this.encryptedContent;
  }
  
  public AlgorithmIdentifier getEncryptionAlgorithm() {
    return this.encryptionAlgorithm;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.encryptionAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.encryptedContent);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
