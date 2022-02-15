package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

public class POPODecKeyRespContent extends ASN1Object {
  private ASN1Sequence content;
  
  private POPODecKeyRespContent(ASN1Sequence paramASN1Sequence) {
    this.content = paramASN1Sequence;
  }
  
  public static POPODecKeyRespContent getInstance(Object paramObject) {
    return (paramObject instanceof POPODecKeyRespContent) ? (POPODecKeyRespContent)paramObject : ((paramObject != null) ? new POPODecKeyRespContent(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer[] toASN1IntegerArray() {
    ASN1Integer[] arrayOfASN1Integer = new ASN1Integer[this.content.size()];
    for (byte b = 0; b != arrayOfASN1Integer.length; b++)
      arrayOfASN1Integer[b] = ASN1Integer.getInstance(this.content.getObjectAt(b)); 
    return arrayOfASN1Integer;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.content;
  }
}
