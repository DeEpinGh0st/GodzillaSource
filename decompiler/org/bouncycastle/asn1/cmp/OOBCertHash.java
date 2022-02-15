package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class OOBCertHash extends ASN1Object {
  private AlgorithmIdentifier hashAlg;
  
  private CertId certId;
  
  private DERBitString hashVal;
  
  private OOBCertHash(ASN1Sequence paramASN1Sequence) {
    int i = paramASN1Sequence.size() - 1;
    this.hashVal = DERBitString.getInstance(paramASN1Sequence.getObjectAt(i--));
    for (int j = i; j >= 0; j--) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramASN1Sequence.getObjectAt(j);
      if (aSN1TaggedObject.getTagNo() == 0) {
        this.hashAlg = AlgorithmIdentifier.getInstance(aSN1TaggedObject, true);
      } else {
        this.certId = CertId.getInstance(aSN1TaggedObject, true);
      } 
    } 
  }
  
  public static OOBCertHash getInstance(Object paramObject) {
    return (paramObject instanceof OOBCertHash) ? (OOBCertHash)paramObject : ((paramObject != null) ? new OOBCertHash(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public OOBCertHash(AlgorithmIdentifier paramAlgorithmIdentifier, CertId paramCertId, byte[] paramArrayOfbyte) {
    this(paramAlgorithmIdentifier, paramCertId, new DERBitString(paramArrayOfbyte));
  }
  
  public OOBCertHash(AlgorithmIdentifier paramAlgorithmIdentifier, CertId paramCertId, DERBitString paramDERBitString) {
    this.hashAlg = paramAlgorithmIdentifier;
    this.certId = paramCertId;
    this.hashVal = paramDERBitString;
  }
  
  public AlgorithmIdentifier getHashAlg() {
    return this.hashAlg;
  }
  
  public CertId getCertId() {
    return this.certId;
  }
  
  public DERBitString getHashVal() {
    return this.hashVal;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    addOptional(aSN1EncodableVector, 0, (ASN1Encodable)this.hashAlg);
    addOptional(aSN1EncodableVector, 1, (ASN1Encodable)this.certId);
    aSN1EncodableVector.add((ASN1Encodable)this.hashVal);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private void addOptional(ASN1EncodableVector paramASN1EncodableVector, int paramInt, ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable != null)
      paramASN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, paramInt, paramASN1Encodable)); 
  }
}
