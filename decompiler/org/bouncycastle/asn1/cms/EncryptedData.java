package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;

public class EncryptedData extends ASN1Object {
  private ASN1Integer version;
  
  private EncryptedContentInfo encryptedContentInfo;
  
  private ASN1Set unprotectedAttrs;
  
  public static EncryptedData getInstance(Object paramObject) {
    return (paramObject instanceof EncryptedData) ? (EncryptedData)paramObject : ((paramObject != null) ? new EncryptedData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public EncryptedData(EncryptedContentInfo paramEncryptedContentInfo) {
    this(paramEncryptedContentInfo, null);
  }
  
  public EncryptedData(EncryptedContentInfo paramEncryptedContentInfo, ASN1Set paramASN1Set) {
    this.version = new ASN1Integer((paramASN1Set == null) ? 0L : 2L);
    this.encryptedContentInfo = paramEncryptedContentInfo;
    this.unprotectedAttrs = paramASN1Set;
  }
  
  private EncryptedData(ASN1Sequence paramASN1Sequence) {
    this.version = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0));
    this.encryptedContentInfo = EncryptedContentInfo.getInstance(paramASN1Sequence.getObjectAt(1));
    if (paramASN1Sequence.size() == 3)
      this.unprotectedAttrs = ASN1Set.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(2), false); 
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public EncryptedContentInfo getEncryptedContentInfo() {
    return this.encryptedContentInfo;
  }
  
  public ASN1Set getUnprotectedAttrs() {
    return this.unprotectedAttrs;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    aSN1EncodableVector.add((ASN1Encodable)this.encryptedContentInfo);
    if (this.unprotectedAttrs != null)
      aSN1EncodableVector.add((ASN1Encodable)new BERTaggedObject(false, 1, (ASN1Encodable)this.unprotectedAttrs)); 
    return (ASN1Primitive)new BERSequence(aSN1EncodableVector);
  }
}
