package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class NoticeReference extends ASN1Object {
  private DisplayText organization;
  
  private ASN1Sequence noticeNumbers;
  
  private static ASN1EncodableVector convertVector(Vector paramVector) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    Enumeration<Object> enumeration = paramVector.elements();
    while (enumeration.hasMoreElements()) {
      ASN1Integer aSN1Integer;
      BigInteger bigInteger = (BigInteger)enumeration.nextElement();
      if (bigInteger instanceof BigInteger) {
        aSN1Integer = new ASN1Integer(bigInteger);
      } else if (bigInteger instanceof Integer) {
        aSN1Integer = new ASN1Integer(((Integer)bigInteger).intValue());
      } else {
        throw new IllegalArgumentException();
      } 
      aSN1EncodableVector.add((ASN1Encodable)aSN1Integer);
    } 
    return aSN1EncodableVector;
  }
  
  public NoticeReference(String paramString, Vector paramVector) {
    this(paramString, convertVector(paramVector));
  }
  
  public NoticeReference(String paramString, ASN1EncodableVector paramASN1EncodableVector) {
    this(new DisplayText(paramString), paramASN1EncodableVector);
  }
  
  public NoticeReference(DisplayText paramDisplayText, ASN1EncodableVector paramASN1EncodableVector) {
    this.organization = paramDisplayText;
    this.noticeNumbers = (ASN1Sequence)new DERSequence(paramASN1EncodableVector);
  }
  
  private NoticeReference(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.organization = DisplayText.getInstance(paramASN1Sequence.getObjectAt(0));
    this.noticeNumbers = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static NoticeReference getInstance(Object paramObject) {
    return (paramObject instanceof NoticeReference) ? (NoticeReference)paramObject : ((paramObject != null) ? new NoticeReference(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public DisplayText getOrganization() {
    return this.organization;
  }
  
  public ASN1Integer[] getNoticeNumbers() {
    ASN1Integer[] arrayOfASN1Integer = new ASN1Integer[this.noticeNumbers.size()];
    for (byte b = 0; b != this.noticeNumbers.size(); b++)
      arrayOfASN1Integer[b] = ASN1Integer.getInstance(this.noticeNumbers.getObjectAt(b)); 
    return arrayOfASN1Integer;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.organization);
    aSN1EncodableVector.add((ASN1Encodable)this.noticeNumbers);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
