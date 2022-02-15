package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

public class POPODecKeyChallContent extends ASN1Object {
  private ASN1Sequence content;
  
  private POPODecKeyChallContent(ASN1Sequence paramASN1Sequence) {
    this.content = paramASN1Sequence;
  }
  
  public static POPODecKeyChallContent getInstance(Object paramObject) {
    return (paramObject instanceof POPODecKeyChallContent) ? (POPODecKeyChallContent)paramObject : ((paramObject != null) ? new POPODecKeyChallContent(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public Challenge[] toChallengeArray() {
    Challenge[] arrayOfChallenge = new Challenge[this.content.size()];
    for (byte b = 0; b != arrayOfChallenge.length; b++)
      arrayOfChallenge[b] = Challenge.getInstance(this.content.getObjectAt(b)); 
    return arrayOfChallenge;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.content;
  }
}
