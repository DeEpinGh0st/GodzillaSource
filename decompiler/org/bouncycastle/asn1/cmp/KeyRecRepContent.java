package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class KeyRecRepContent extends ASN1Object {
  private PKIStatusInfo status;
  
  private CMPCertificate newSigCert;
  
  private ASN1Sequence caCerts;
  
  private ASN1Sequence keyPairHist;
  
  private KeyRecRepContent(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.status = PKIStatusInfo.getInstance(enumeration.nextElement());
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(enumeration.nextElement());
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.newSigCert = CMPCertificate.getInstance(aSN1TaggedObject.getObject());
          continue;
        case 1:
          this.caCerts = ASN1Sequence.getInstance(aSN1TaggedObject.getObject());
          continue;
        case 2:
          this.keyPairHist = ASN1Sequence.getInstance(aSN1TaggedObject.getObject());
          continue;
      } 
      throw new IllegalArgumentException("unknown tag number: " + aSN1TaggedObject.getTagNo());
    } 
  }
  
  public static KeyRecRepContent getInstance(Object paramObject) {
    return (paramObject instanceof KeyRecRepContent) ? (KeyRecRepContent)paramObject : ((paramObject != null) ? new KeyRecRepContent(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public PKIStatusInfo getStatus() {
    return this.status;
  }
  
  public CMPCertificate getNewSigCert() {
    return this.newSigCert;
  }
  
  public CMPCertificate[] getCaCerts() {
    if (this.caCerts == null)
      return null; 
    CMPCertificate[] arrayOfCMPCertificate = new CMPCertificate[this.caCerts.size()];
    for (byte b = 0; b != arrayOfCMPCertificate.length; b++)
      arrayOfCMPCertificate[b] = CMPCertificate.getInstance(this.caCerts.getObjectAt(b)); 
    return arrayOfCMPCertificate;
  }
  
  public CertifiedKeyPair[] getKeyPairHist() {
    if (this.keyPairHist == null)
      return null; 
    CertifiedKeyPair[] arrayOfCertifiedKeyPair = new CertifiedKeyPair[this.keyPairHist.size()];
    for (byte b = 0; b != arrayOfCertifiedKeyPair.length; b++)
      arrayOfCertifiedKeyPair[b] = CertifiedKeyPair.getInstance(this.keyPairHist.getObjectAt(b)); 
    return arrayOfCertifiedKeyPair;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.status);
    addOptional(aSN1EncodableVector, 0, (ASN1Encodable)this.newSigCert);
    addOptional(aSN1EncodableVector, 1, (ASN1Encodable)this.caCerts);
    addOptional(aSN1EncodableVector, 2, (ASN1Encodable)this.keyPairHist);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private void addOptional(ASN1EncodableVector paramASN1EncodableVector, int paramInt, ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable != null)
      paramASN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, paramInt, paramASN1Encodable)); 
  }
}
