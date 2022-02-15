package org.bouncycastle.asn1.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;

public class PKIStatusInfo extends ASN1Object {
  ASN1Integer status;
  
  PKIFreeText statusString;
  
  DERBitString failInfo;
  
  public static PKIStatusInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static PKIStatusInfo getInstance(Object paramObject) {
    return (paramObject instanceof PKIStatusInfo) ? (PKIStatusInfo)paramObject : ((paramObject != null) ? new PKIStatusInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private PKIStatusInfo(ASN1Sequence paramASN1Sequence) {
    this.status = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0));
    this.statusString = null;
    this.failInfo = null;
    if (paramASN1Sequence.size() > 2) {
      this.statusString = PKIFreeText.getInstance(paramASN1Sequence.getObjectAt(1));
      this.failInfo = DERBitString.getInstance(paramASN1Sequence.getObjectAt(2));
    } else if (paramASN1Sequence.size() > 1) {
      ASN1Encodable aSN1Encodable = paramASN1Sequence.getObjectAt(1);
      if (aSN1Encodable instanceof DERBitString) {
        this.failInfo = DERBitString.getInstance(aSN1Encodable);
      } else {
        this.statusString = PKIFreeText.getInstance(aSN1Encodable);
      } 
    } 
  }
  
  public PKIStatusInfo(PKIStatus paramPKIStatus) {
    this.status = ASN1Integer.getInstance(paramPKIStatus.toASN1Primitive());
  }
  
  public PKIStatusInfo(PKIStatus paramPKIStatus, PKIFreeText paramPKIFreeText) {
    this.status = ASN1Integer.getInstance(paramPKIStatus.toASN1Primitive());
    this.statusString = paramPKIFreeText;
  }
  
  public PKIStatusInfo(PKIStatus paramPKIStatus, PKIFreeText paramPKIFreeText, PKIFailureInfo paramPKIFailureInfo) {
    this.status = ASN1Integer.getInstance(paramPKIStatus.toASN1Primitive());
    this.statusString = paramPKIFreeText;
    this.failInfo = paramPKIFailureInfo;
  }
  
  public BigInteger getStatus() {
    return this.status.getValue();
  }
  
  public PKIFreeText getStatusString() {
    return this.statusString;
  }
  
  public DERBitString getFailInfo() {
    return this.failInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.status);
    if (this.statusString != null)
      aSN1EncodableVector.add((ASN1Encodable)this.statusString); 
    if (this.failInfo != null)
      aSN1EncodableVector.add((ASN1Encodable)this.failInfo); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
