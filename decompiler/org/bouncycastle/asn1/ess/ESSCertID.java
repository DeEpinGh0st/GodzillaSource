package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.IssuerSerial;

public class ESSCertID extends ASN1Object {
  private ASN1OctetString certHash;
  
  private IssuerSerial issuerSerial;
  
  public static ESSCertID getInstance(Object paramObject) {
    return (paramObject instanceof ESSCertID) ? (ESSCertID)paramObject : ((paramObject != null) ? new ESSCertID(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private ESSCertID(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 1 || paramASN1Sequence.size() > 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.certHash = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() > 1)
      this.issuerSerial = IssuerSerial.getInstance(paramASN1Sequence.getObjectAt(1)); 
  }
  
  public ESSCertID(byte[] paramArrayOfbyte) {
    this.certHash = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
  }
  
  public ESSCertID(byte[] paramArrayOfbyte, IssuerSerial paramIssuerSerial) {
    this.certHash = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
    this.issuerSerial = paramIssuerSerial;
  }
  
  public byte[] getCertHash() {
    return this.certHash.getOctets();
  }
  
  public IssuerSerial getIssuerSerial() {
    return this.issuerSerial;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certHash);
    if (this.issuerSerial != null)
      aSN1EncodableVector.add((ASN1Encodable)this.issuerSerial); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
