package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class GenRepContent extends ASN1Object {
  private ASN1Sequence content;
  
  private GenRepContent(ASN1Sequence paramASN1Sequence) {
    this.content = paramASN1Sequence;
  }
  
  public static GenRepContent getInstance(Object paramObject) {
    return (paramObject instanceof GenRepContent) ? (GenRepContent)paramObject : ((paramObject != null) ? new GenRepContent(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public GenRepContent(InfoTypeAndValue paramInfoTypeAndValue) {
    this.content = (ASN1Sequence)new DERSequence((ASN1Encodable)paramInfoTypeAndValue);
  }
  
  public GenRepContent(InfoTypeAndValue[] paramArrayOfInfoTypeAndValue) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b < paramArrayOfInfoTypeAndValue.length; b++)
      aSN1EncodableVector.add((ASN1Encodable)paramArrayOfInfoTypeAndValue[b]); 
    this.content = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  public InfoTypeAndValue[] toInfoTypeAndValueArray() {
    InfoTypeAndValue[] arrayOfInfoTypeAndValue = new InfoTypeAndValue[this.content.size()];
    for (byte b = 0; b != arrayOfInfoTypeAndValue.length; b++)
      arrayOfInfoTypeAndValue[b] = InfoTypeAndValue.getInstance(this.content.getObjectAt(b)); 
    return arrayOfInfoTypeAndValue;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.content;
  }
}
