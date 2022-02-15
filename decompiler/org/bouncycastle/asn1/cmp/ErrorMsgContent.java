package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ErrorMsgContent extends ASN1Object {
  private PKIStatusInfo pkiStatusInfo;
  
  private ASN1Integer errorCode;
  
  private PKIFreeText errorDetails;
  
  private ErrorMsgContent(ASN1Sequence paramASN1Sequence) {
    Enumeration<Object> enumeration = paramASN1Sequence.getObjects();
    this.pkiStatusInfo = PKIStatusInfo.getInstance(enumeration.nextElement());
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      if (object instanceof ASN1Integer) {
        this.errorCode = ASN1Integer.getInstance(object);
        continue;
      } 
      this.errorDetails = PKIFreeText.getInstance(object);
    } 
  }
  
  public static ErrorMsgContent getInstance(Object paramObject) {
    return (paramObject instanceof ErrorMsgContent) ? (ErrorMsgContent)paramObject : ((paramObject != null) ? new ErrorMsgContent(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ErrorMsgContent(PKIStatusInfo paramPKIStatusInfo) {
    this(paramPKIStatusInfo, null, null);
  }
  
  public ErrorMsgContent(PKIStatusInfo paramPKIStatusInfo, ASN1Integer paramASN1Integer, PKIFreeText paramPKIFreeText) {
    if (paramPKIStatusInfo == null)
      throw new IllegalArgumentException("'pkiStatusInfo' cannot be null"); 
    this.pkiStatusInfo = paramPKIStatusInfo;
    this.errorCode = paramASN1Integer;
    this.errorDetails = paramPKIFreeText;
  }
  
  public PKIStatusInfo getPKIStatusInfo() {
    return this.pkiStatusInfo;
  }
  
  public ASN1Integer getErrorCode() {
    return this.errorCode;
  }
  
  public PKIFreeText getErrorDetails() {
    return this.errorDetails;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.pkiStatusInfo);
    addOptional(aSN1EncodableVector, (ASN1Encodable)this.errorCode);
    addOptional(aSN1EncodableVector, (ASN1Encodable)this.errorDetails);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private void addOptional(ASN1EncodableVector paramASN1EncodableVector, ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable != null)
      paramASN1EncodableVector.add(paramASN1Encodable); 
  }
}
