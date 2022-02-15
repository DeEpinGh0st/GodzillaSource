package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.DisplayText;
import org.bouncycastle.asn1.x509.NoticeReference;

public class SPUserNotice extends ASN1Object {
  private NoticeReference noticeRef;
  
  private DisplayText explicitText;
  
  public static SPUserNotice getInstance(Object paramObject) {
    return (paramObject instanceof SPUserNotice) ? (SPUserNotice)paramObject : ((paramObject != null) ? new SPUserNotice(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private SPUserNotice(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1Encodable> enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1Encodable aSN1Encodable = enumeration.nextElement();
      if (aSN1Encodable instanceof DisplayText || aSN1Encodable instanceof org.bouncycastle.asn1.ASN1String) {
        this.explicitText = DisplayText.getInstance(aSN1Encodable);
        continue;
      } 
      if (aSN1Encodable instanceof NoticeReference || aSN1Encodable instanceof ASN1Sequence) {
        this.noticeRef = NoticeReference.getInstance(aSN1Encodable);
        continue;
      } 
      throw new IllegalArgumentException("Invalid element in 'SPUserNotice': " + aSN1Encodable.getClass().getName());
    } 
  }
  
  public SPUserNotice(NoticeReference paramNoticeReference, DisplayText paramDisplayText) {
    this.noticeRef = paramNoticeReference;
    this.explicitText = paramDisplayText;
  }
  
  public NoticeReference getNoticeRef() {
    return this.noticeRef;
  }
  
  public DisplayText getExplicitText() {
    return this.explicitText;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.noticeRef != null)
      aSN1EncodableVector.add((ASN1Encodable)this.noticeRef); 
    if (this.explicitText != null)
      aSN1EncodableVector.add((ASN1Encodable)this.explicitText); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
