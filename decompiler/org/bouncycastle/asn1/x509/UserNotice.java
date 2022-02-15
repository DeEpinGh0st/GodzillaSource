package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class UserNotice extends ASN1Object {
  private final NoticeReference noticeRef;
  
  private final DisplayText explicitText;
  
  public UserNotice(NoticeReference paramNoticeReference, DisplayText paramDisplayText) {
    this.noticeRef = paramNoticeReference;
    this.explicitText = paramDisplayText;
  }
  
  public UserNotice(NoticeReference paramNoticeReference, String paramString) {
    this(paramNoticeReference, new DisplayText(paramString));
  }
  
  private UserNotice(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() == 2) {
      this.noticeRef = NoticeReference.getInstance(paramASN1Sequence.getObjectAt(0));
      this.explicitText = DisplayText.getInstance(paramASN1Sequence.getObjectAt(1));
    } else if (paramASN1Sequence.size() == 1) {
      if (paramASN1Sequence.getObjectAt(0).toASN1Primitive() instanceof ASN1Sequence) {
        this.noticeRef = NoticeReference.getInstance(paramASN1Sequence.getObjectAt(0));
        this.explicitText = null;
      } else {
        this.noticeRef = null;
        this.explicitText = DisplayText.getInstance(paramASN1Sequence.getObjectAt(0));
      } 
    } else if (paramASN1Sequence.size() == 0) {
      this.noticeRef = null;
      this.explicitText = null;
    } else {
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size());
    } 
  }
  
  public static UserNotice getInstance(Object paramObject) {
    return (paramObject instanceof UserNotice) ? (UserNotice)paramObject : ((paramObject != null) ? new UserNotice(ASN1Sequence.getInstance(paramObject)) : null);
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
