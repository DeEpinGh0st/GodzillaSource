package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.IssuerSerial;

public class OtherCertID extends ASN1Object {
  private ASN1Encodable otherCertHash;
  
  private IssuerSerial issuerSerial;
  
  public static OtherCertID getInstance(Object paramObject) {
    return (paramObject instanceof OtherCertID) ? (OtherCertID)paramObject : ((paramObject != null) ? new OtherCertID(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private OtherCertID(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 1 || paramASN1Sequence.size() > 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    if (paramASN1Sequence.getObjectAt(0).toASN1Primitive() instanceof ASN1OctetString) {
      this.otherCertHash = (ASN1Encodable)ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(0));
    } else {
      this.otherCertHash = (ASN1Encodable)DigestInfo.getInstance(paramASN1Sequence.getObjectAt(0));
    } 
    if (paramASN1Sequence.size() > 1)
      this.issuerSerial = IssuerSerial.getInstance(paramASN1Sequence.getObjectAt(1)); 
  }
  
  public OtherCertID(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) {
    this.otherCertHash = (ASN1Encodable)new DigestInfo(paramAlgorithmIdentifier, paramArrayOfbyte);
  }
  
  public OtherCertID(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte, IssuerSerial paramIssuerSerial) {
    this.otherCertHash = (ASN1Encodable)new DigestInfo(paramAlgorithmIdentifier, paramArrayOfbyte);
    this.issuerSerial = paramIssuerSerial;
  }
  
  public AlgorithmIdentifier getAlgorithmHash() {
    return (this.otherCertHash.toASN1Primitive() instanceof ASN1OctetString) ? new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1) : DigestInfo.getInstance(this.otherCertHash).getAlgorithmId();
  }
  
  public byte[] getCertHash() {
    return (this.otherCertHash.toASN1Primitive() instanceof ASN1OctetString) ? ((ASN1OctetString)this.otherCertHash.toASN1Primitive()).getOctets() : DigestInfo.getInstance(this.otherCertHash).getDigest();
  }
  
  public IssuerSerial getIssuerSerial() {
    return this.issuerSerial;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add(this.otherCertHash);
    if (this.issuerSerial != null)
      aSN1EncodableVector.add((ASN1Encodable)this.issuerSerial); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
