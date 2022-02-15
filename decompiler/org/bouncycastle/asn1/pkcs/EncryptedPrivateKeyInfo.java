package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class EncryptedPrivateKeyInfo extends ASN1Object {
  private AlgorithmIdentifier algId;
  
  private ASN1OctetString data;
  
  private EncryptedPrivateKeyInfo(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.algId = AlgorithmIdentifier.getInstance(enumeration.nextElement());
    this.data = ASN1OctetString.getInstance(enumeration.nextElement());
  }
  
  public EncryptedPrivateKeyInfo(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) {
    this.algId = paramAlgorithmIdentifier;
    this.data = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
  }
  
  public static EncryptedPrivateKeyInfo getInstance(Object paramObject) {
    return (paramObject instanceof EncryptedPrivateKeyInfo) ? (EncryptedPrivateKeyInfo)paramObject : ((paramObject != null) ? new EncryptedPrivateKeyInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public AlgorithmIdentifier getEncryptionAlgorithm() {
    return this.algId;
  }
  
  public byte[] getEncryptedData() {
    return this.data.getOctets();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.algId);
    aSN1EncodableVector.add((ASN1Encodable)this.data);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
