package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CertificationRequest extends ASN1Object {
  protected CertificationRequestInfo reqInfo = null;
  
  protected AlgorithmIdentifier sigAlgId = null;
  
  protected DERBitString sigBits = null;
  
  public static CertificationRequest getInstance(Object paramObject) {
    return (paramObject instanceof CertificationRequest) ? (CertificationRequest)paramObject : ((paramObject != null) ? new CertificationRequest(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  protected CertificationRequest() {}
  
  public CertificationRequest(CertificationRequestInfo paramCertificationRequestInfo, AlgorithmIdentifier paramAlgorithmIdentifier, DERBitString paramDERBitString) {
    this.reqInfo = paramCertificationRequestInfo;
    this.sigAlgId = paramAlgorithmIdentifier;
    this.sigBits = paramDERBitString;
  }
  
  public CertificationRequest(ASN1Sequence paramASN1Sequence) {
    this.reqInfo = CertificationRequestInfo.getInstance(paramASN1Sequence.getObjectAt(0));
    this.sigAlgId = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.sigBits = (DERBitString)paramASN1Sequence.getObjectAt(2);
  }
  
  public CertificationRequestInfo getCertificationRequestInfo() {
    return this.reqInfo;
  }
  
  public AlgorithmIdentifier getSignatureAlgorithm() {
    return this.sigAlgId;
  }
  
  public DERBitString getSignature() {
    return this.sigBits;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.reqInfo);
    aSN1EncodableVector.add((ASN1Encodable)this.sigAlgId);
    aSN1EncodableVector.add((ASN1Encodable)this.sigBits);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
