package org.bouncycastle.asn1.crmf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class CertReqMsg extends ASN1Object {
  private CertRequest certReq;
  
  private ProofOfPossession pop;
  
  private ASN1Sequence regInfo;
  
  private CertReqMsg(ASN1Sequence paramASN1Sequence) {
    Enumeration<Object> enumeration = paramASN1Sequence.getObjects();
    this.certReq = CertRequest.getInstance(enumeration.nextElement());
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      if (object instanceof ASN1TaggedObject || object instanceof ProofOfPossession) {
        this.pop = ProofOfPossession.getInstance(object);
        continue;
      } 
      this.regInfo = ASN1Sequence.getInstance(object);
    } 
  }
  
  public static CertReqMsg getInstance(Object paramObject) {
    return (paramObject instanceof CertReqMsg) ? (CertReqMsg)paramObject : ((paramObject != null) ? new CertReqMsg(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static CertReqMsg getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public CertReqMsg(CertRequest paramCertRequest, ProofOfPossession paramProofOfPossession, AttributeTypeAndValue[] paramArrayOfAttributeTypeAndValue) {
    if (paramCertRequest == null)
      throw new IllegalArgumentException("'certReq' cannot be null"); 
    this.certReq = paramCertRequest;
    this.pop = paramProofOfPossession;
    if (paramArrayOfAttributeTypeAndValue != null)
      this.regInfo = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfAttributeTypeAndValue); 
  }
  
  public CertRequest getCertReq() {
    return this.certReq;
  }
  
  public ProofOfPossession getPop() {
    return this.pop;
  }
  
  public ProofOfPossession getPopo() {
    return this.pop;
  }
  
  public AttributeTypeAndValue[] getRegInfo() {
    if (this.regInfo == null)
      return null; 
    AttributeTypeAndValue[] arrayOfAttributeTypeAndValue = new AttributeTypeAndValue[this.regInfo.size()];
    for (byte b = 0; b != arrayOfAttributeTypeAndValue.length; b++)
      arrayOfAttributeTypeAndValue[b] = AttributeTypeAndValue.getInstance(this.regInfo.getObjectAt(b)); 
    return arrayOfAttributeTypeAndValue;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certReq);
    addOptional(aSN1EncodableVector, (ASN1Encodable)this.pop);
    addOptional(aSN1EncodableVector, (ASN1Encodable)this.regInfo);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private void addOptional(ASN1EncodableVector paramASN1EncodableVector, ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable != null)
      paramASN1EncodableVector.add(paramASN1Encodable); 
  }
}
