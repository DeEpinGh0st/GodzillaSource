package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.x509.Certificate;

public class EncryptedPrivateKeyData extends ASN1Object {
  private final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;
  
  private final Certificate[] certificateChain;
  
  public EncryptedPrivateKeyData(EncryptedPrivateKeyInfo paramEncryptedPrivateKeyInfo, Certificate[] paramArrayOfCertificate) {
    this.encryptedPrivateKeyInfo = paramEncryptedPrivateKeyInfo;
    this.certificateChain = new Certificate[paramArrayOfCertificate.length];
    System.arraycopy(paramArrayOfCertificate, 0, this.certificateChain, 0, paramArrayOfCertificate.length);
  }
  
  private EncryptedPrivateKeyData(ASN1Sequence paramASN1Sequence) {
    this.encryptedPrivateKeyInfo = EncryptedPrivateKeyInfo.getInstance(paramASN1Sequence.getObjectAt(0));
    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(1));
    this.certificateChain = new Certificate[aSN1Sequence.size()];
    for (byte b = 0; b != this.certificateChain.length; b++)
      this.certificateChain[b] = Certificate.getInstance(aSN1Sequence.getObjectAt(b)); 
  }
  
  public static EncryptedPrivateKeyData getInstance(Object paramObject) {
    return (paramObject instanceof EncryptedPrivateKeyData) ? (EncryptedPrivateKeyData)paramObject : ((paramObject != null) ? new EncryptedPrivateKeyData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public Certificate[] getCertificateChain() {
    Certificate[] arrayOfCertificate = new Certificate[this.certificateChain.length];
    System.arraycopy(this.certificateChain, 0, arrayOfCertificate, 0, this.certificateChain.length);
    return arrayOfCertificate;
  }
  
  public EncryptedPrivateKeyInfo getEncryptedPrivateKeyInfo() {
    return this.encryptedPrivateKeyInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.encryptedPrivateKeyInfo);
    aSN1EncodableVector.add((ASN1Encodable)new DERSequence((ASN1Encodable[])this.certificateChain));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
