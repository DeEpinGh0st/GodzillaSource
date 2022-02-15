package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class PollRepContent extends ASN1Object {
  private ASN1Integer[] certReqId;
  
  private ASN1Integer[] checkAfter;
  
  private PKIFreeText[] reason;
  
  private PollRepContent(ASN1Sequence paramASN1Sequence) {
    this.certReqId = new ASN1Integer[paramASN1Sequence.size()];
    this.checkAfter = new ASN1Integer[paramASN1Sequence.size()];
    this.reason = new PKIFreeText[paramASN1Sequence.size()];
    for (byte b = 0; b != paramASN1Sequence.size(); b++) {
      ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(b));
      this.certReqId[b] = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
      this.checkAfter[b] = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1));
      if (aSN1Sequence.size() > 2)
        this.reason[b] = PKIFreeText.getInstance(aSN1Sequence.getObjectAt(2)); 
    } 
  }
  
  public static PollRepContent getInstance(Object paramObject) {
    return (paramObject instanceof PollRepContent) ? (PollRepContent)paramObject : ((paramObject != null) ? new PollRepContent(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public PollRepContent(ASN1Integer paramASN1Integer1, ASN1Integer paramASN1Integer2) {
    this(paramASN1Integer1, paramASN1Integer2, null);
  }
  
  public PollRepContent(ASN1Integer paramASN1Integer1, ASN1Integer paramASN1Integer2, PKIFreeText paramPKIFreeText) {
    this.certReqId = new ASN1Integer[1];
    this.checkAfter = new ASN1Integer[1];
    this.reason = new PKIFreeText[1];
    this.certReqId[0] = paramASN1Integer1;
    this.checkAfter[0] = paramASN1Integer2;
    this.reason[0] = paramPKIFreeText;
  }
  
  public int size() {
    return this.certReqId.length;
  }
  
  public ASN1Integer getCertReqId(int paramInt) {
    return this.certReqId[paramInt];
  }
  
  public ASN1Integer getCheckAfter(int paramInt) {
    return this.checkAfter[paramInt];
  }
  
  public PKIFreeText getReason(int paramInt) {
    return this.reason[paramInt];
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b != this.certReqId.length; b++) {
      ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
      aSN1EncodableVector1.add((ASN1Encodable)this.certReqId[b]);
      aSN1EncodableVector1.add((ASN1Encodable)this.checkAfter[b]);
      if (this.reason[b] != null)
        aSN1EncodableVector1.add((ASN1Encodable)this.reason[b]); 
      aSN1EncodableVector.add((ASN1Encodable)new DERSequence(aSN1EncodableVector1));
    } 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
