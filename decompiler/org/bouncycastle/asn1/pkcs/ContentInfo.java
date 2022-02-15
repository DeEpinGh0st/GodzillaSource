package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DLSequence;

public class ContentInfo extends ASN1Object implements PKCSObjectIdentifiers {
  private ASN1ObjectIdentifier contentType;
  
  private ASN1Encodable content;
  
  private boolean isBer = true;
  
  public static ContentInfo getInstance(Object paramObject) {
    return (paramObject instanceof ContentInfo) ? (ContentInfo)paramObject : ((paramObject != null) ? new ContentInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private ContentInfo(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1ObjectIdentifier> enumeration = paramASN1Sequence.getObjects();
    this.contentType = enumeration.nextElement();
    if (enumeration.hasMoreElements())
      this.content = (ASN1Encodable)((ASN1TaggedObject)enumeration.nextElement()).getObject(); 
    this.isBer = paramASN1Sequence instanceof BERSequence;
  }
  
  public ContentInfo(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.contentType = paramASN1ObjectIdentifier;
    this.content = paramASN1Encodable;
  }
  
  public ASN1ObjectIdentifier getContentType() {
    return this.contentType;
  }
  
  public ASN1Encodable getContent() {
    return this.content;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.contentType);
    if (this.content != null)
      aSN1EncodableVector.add((ASN1Encodable)new BERTaggedObject(true, 0, this.content)); 
    return (ASN1Primitive)(this.isBer ? new BERSequence(aSN1EncodableVector) : new DLSequence(aSN1EncodableVector));
  }
}
