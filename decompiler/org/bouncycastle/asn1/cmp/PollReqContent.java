package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class PollReqContent extends ASN1Object {
  private ASN1Sequence content;
  
  private PollReqContent(ASN1Sequence paramASN1Sequence) {
    this.content = paramASN1Sequence;
  }
  
  public static PollReqContent getInstance(Object paramObject) {
    return (paramObject instanceof PollReqContent) ? (PollReqContent)paramObject : ((paramObject != null) ? new PollReqContent(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public PollReqContent(ASN1Integer paramASN1Integer) {
    this((ASN1Sequence)new DERSequence((ASN1Encodable)new DERSequence((ASN1Encodable)paramASN1Integer)));
  }
  
  public ASN1Integer[][] getCertReqIds() {
    ASN1Integer[][] arrayOfASN1Integer = new ASN1Integer[this.content.size()][];
    for (byte b = 0; b != arrayOfASN1Integer.length; b++)
      arrayOfASN1Integer[b] = sequenceToASN1IntegerArray((ASN1Sequence)this.content.getObjectAt(b)); 
    return arrayOfASN1Integer;
  }
  
  private static ASN1Integer[] sequenceToASN1IntegerArray(ASN1Sequence paramASN1Sequence) {
    ASN1Integer[] arrayOfASN1Integer = new ASN1Integer[paramASN1Sequence.size()];
    for (byte b = 0; b != arrayOfASN1Integer.length; b++)
      arrayOfASN1Integer[b] = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(b)); 
    return arrayOfASN1Integer;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.content;
  }
}
