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
import org.bouncycastle.util.Arrays;

public class EncryptedSecretKeyData extends ASN1Object {
  private final AlgorithmIdentifier keyEncryptionAlgorithm;
  
  private final ASN1OctetString encryptedKeyData;
  
  public EncryptedSecretKeyData(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) {
    this.keyEncryptionAlgorithm = paramAlgorithmIdentifier;
    this.encryptedKeyData = (ASN1OctetString)new DEROctetString(Arrays.clone(paramArrayOfbyte));
  }
  
  private EncryptedSecretKeyData(ASN1Sequence paramASN1Sequence) {
    this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.encryptedKeyData = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static EncryptedSecretKeyData getInstance(Object paramObject) {
    return (paramObject instanceof EncryptedSecretKeyData) ? (EncryptedSecretKeyData)paramObject : ((paramObject != null) ? new EncryptedSecretKeyData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
    return this.keyEncryptionAlgorithm;
  }
  
  public byte[] getEncryptedKeyData() {
    return Arrays.clone(this.encryptedKeyData.getOctets());
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.keyEncryptionAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.encryptedKeyData);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
