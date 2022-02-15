package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CertRequest extends ASN1Object {
  private ASN1Integer certReqId;
  
  private CertTemplate certTemplate;
  
  private Controls controls;
  
  private CertRequest(ASN1Sequence paramASN1Sequence) {
    this.certReqId = new ASN1Integer(ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0)).getValue());
    this.certTemplate = CertTemplate.getInstance(paramASN1Sequence.getObjectAt(1));
    if (paramASN1Sequence.size() > 2)
      this.controls = Controls.getInstance(paramASN1Sequence.getObjectAt(2)); 
  }
  
  public static CertRequest getInstance(Object paramObject) {
    return (paramObject instanceof CertRequest) ? (CertRequest)paramObject : ((paramObject != null) ? new CertRequest(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public CertRequest(int paramInt, CertTemplate paramCertTemplate, Controls paramControls) {
    this(new ASN1Integer(paramInt), paramCertTemplate, paramControls);
  }
  
  public CertRequest(ASN1Integer paramASN1Integer, CertTemplate paramCertTemplate, Controls paramControls) {
    this.certReqId = paramASN1Integer;
    this.certTemplate = paramCertTemplate;
    this.controls = paramControls;
  }
  
  public ASN1Integer getCertReqId() {
    return this.certReqId;
  }
  
  public CertTemplate getCertTemplate() {
    return this.certTemplate;
  }
  
  public Controls getControls() {
    return this.controls;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certReqId);
    aSN1EncodableVector.add((ASN1Encodable)this.certTemplate);
    if (this.controls != null)
      aSN1EncodableVector.add((ASN1Encodable)this.controls); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
