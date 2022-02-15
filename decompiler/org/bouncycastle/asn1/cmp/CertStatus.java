package org.bouncycastle.asn1.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

public class CertStatus extends ASN1Object {
  private ASN1OctetString certHash;
  
  private ASN1Integer certReqId;
  
  private PKIStatusInfo statusInfo;
  
  private CertStatus(ASN1Sequence paramASN1Sequence) {
    this.certHash = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(0));
    this.certReqId = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(1));
    if (paramASN1Sequence.size() > 2)
      this.statusInfo = PKIStatusInfo.getInstance(paramASN1Sequence.getObjectAt(2)); 
  }
  
  public CertStatus(byte[] paramArrayOfbyte, BigInteger paramBigInteger) {
    this.certHash = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
    this.certReqId = new ASN1Integer(paramBigInteger);
  }
  
  public CertStatus(byte[] paramArrayOfbyte, BigInteger paramBigInteger, PKIStatusInfo paramPKIStatusInfo) {
    this.certHash = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
    this.certReqId = new ASN1Integer(paramBigInteger);
    this.statusInfo = paramPKIStatusInfo;
  }
  
  public static CertStatus getInstance(Object paramObject) {
    return (paramObject instanceof CertStatus) ? (CertStatus)paramObject : ((paramObject != null) ? new CertStatus(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1OctetString getCertHash() {
    return this.certHash;
  }
  
  public ASN1Integer getCertReqId() {
    return this.certReqId;
  }
  
  public PKIStatusInfo getStatusInfo() {
    return this.statusInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certHash);
    aSN1EncodableVector.add((ASN1Encodable)this.certReqId);
    if (this.statusInfo != null)
      aSN1EncodableVector.add((ASN1Encodable)this.statusInfo); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
