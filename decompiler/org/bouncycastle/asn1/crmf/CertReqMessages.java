package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CertReqMessages extends ASN1Object {
  private ASN1Sequence content;
  
  private CertReqMessages(ASN1Sequence paramASN1Sequence) {
    this.content = paramASN1Sequence;
  }
  
  public static CertReqMessages getInstance(Object paramObject) {
    return (paramObject instanceof CertReqMessages) ? (CertReqMessages)paramObject : ((paramObject != null) ? new CertReqMessages(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public CertReqMessages(CertReqMsg paramCertReqMsg) {
    this.content = (ASN1Sequence)new DERSequence((ASN1Encodable)paramCertReqMsg);
  }
  
  public CertReqMessages(CertReqMsg[] paramArrayOfCertReqMsg) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b < paramArrayOfCertReqMsg.length; b++)
      aSN1EncodableVector.add((ASN1Encodable)paramArrayOfCertReqMsg[b]); 
    this.content = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  public CertReqMsg[] toCertReqMsgArray() {
    CertReqMsg[] arrayOfCertReqMsg = new CertReqMsg[this.content.size()];
    for (byte b = 0; b != arrayOfCertReqMsg.length; b++)
      arrayOfCertReqMsg[b] = CertReqMsg.getInstance(this.content.getObjectAt(b)); 
    return arrayOfCertReqMsg;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.content;
  }
}
