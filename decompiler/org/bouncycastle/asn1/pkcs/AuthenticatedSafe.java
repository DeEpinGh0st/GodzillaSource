package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DLSequence;

public class AuthenticatedSafe extends ASN1Object {
  private ContentInfo[] info;
  
  private boolean isBer = true;
  
  private AuthenticatedSafe(ASN1Sequence paramASN1Sequence) {
    this.info = new ContentInfo[paramASN1Sequence.size()];
    for (byte b = 0; b != this.info.length; b++)
      this.info[b] = ContentInfo.getInstance(paramASN1Sequence.getObjectAt(b)); 
    this.isBer = paramASN1Sequence instanceof BERSequence;
  }
  
  public static AuthenticatedSafe getInstance(Object paramObject) {
    return (paramObject instanceof AuthenticatedSafe) ? (AuthenticatedSafe)paramObject : ((paramObject != null) ? new AuthenticatedSafe(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public AuthenticatedSafe(ContentInfo[] paramArrayOfContentInfo) {
    this.info = paramArrayOfContentInfo;
  }
  
  public ContentInfo[] getContentInfo() {
    return this.info;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b != this.info.length; b++)
      aSN1EncodableVector.add((ASN1Encodable)this.info[b]); 
    return (ASN1Primitive)(this.isBer ? new BERSequence(aSN1EncodableVector) : new DLSequence(aSN1EncodableVector));
  }
}
