package org.bouncycastle.asn1.mozilla;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class PublicKeyAndChallenge extends ASN1Object {
  private ASN1Sequence pkacSeq;
  
  private SubjectPublicKeyInfo spki;
  
  private DERIA5String challenge;
  
  public static PublicKeyAndChallenge getInstance(Object paramObject) {
    return (paramObject instanceof PublicKeyAndChallenge) ? (PublicKeyAndChallenge)paramObject : ((paramObject != null) ? new PublicKeyAndChallenge(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private PublicKeyAndChallenge(ASN1Sequence paramASN1Sequence) {
    this.pkacSeq = paramASN1Sequence;
    this.spki = SubjectPublicKeyInfo.getInstance(paramASN1Sequence.getObjectAt(0));
    this.challenge = DERIA5String.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.pkacSeq;
  }
  
  public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
    return this.spki;
  }
  
  public DERIA5String getChallenge() {
    return this.challenge;
  }
}
