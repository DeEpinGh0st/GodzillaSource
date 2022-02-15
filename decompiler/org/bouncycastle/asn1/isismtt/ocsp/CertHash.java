package org.bouncycastle.asn1.isismtt.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CertHash extends ASN1Object {
  private AlgorithmIdentifier hashAlgorithm;
  
  private byte[] certificateHash;
  
  public static CertHash getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof CertHash)
      return (CertHash)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new CertHash((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  private CertHash(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.hashAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.certificateHash = DEROctetString.getInstance(paramASN1Sequence.getObjectAt(1)).getOctets();
  }
  
  public CertHash(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) {
    this.hashAlgorithm = paramAlgorithmIdentifier;
    this.certificateHash = new byte[paramArrayOfbyte.length];
    System.arraycopy(paramArrayOfbyte, 0, this.certificateHash, 0, paramArrayOfbyte.length);
  }
  
  public AlgorithmIdentifier getHashAlgorithm() {
    return this.hashAlgorithm;
  }
  
  public byte[] getCertificateHash() {
    return this.certificateHash;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.hashAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.certificateHash));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
