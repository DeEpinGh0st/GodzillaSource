package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class EnvelopedData extends ASN1Object {
  private ASN1Integer version;
  
  private OriginatorInfo originatorInfo;
  
  private ASN1Set recipientInfos;
  
  private EncryptedContentInfo encryptedContentInfo;
  
  private ASN1Set unprotectedAttrs;
  
  public EnvelopedData(OriginatorInfo paramOriginatorInfo, ASN1Set paramASN1Set1, EncryptedContentInfo paramEncryptedContentInfo, ASN1Set paramASN1Set2) {
    this.version = new ASN1Integer(calculateVersion(paramOriginatorInfo, paramASN1Set1, paramASN1Set2));
    this.originatorInfo = paramOriginatorInfo;
    this.recipientInfos = paramASN1Set1;
    this.encryptedContentInfo = paramEncryptedContentInfo;
    this.unprotectedAttrs = paramASN1Set2;
  }
  
  public EnvelopedData(OriginatorInfo paramOriginatorInfo, ASN1Set paramASN1Set, EncryptedContentInfo paramEncryptedContentInfo, Attributes paramAttributes) {
    this.version = new ASN1Integer(calculateVersion(paramOriginatorInfo, paramASN1Set, ASN1Set.getInstance(paramAttributes)));
    this.originatorInfo = paramOriginatorInfo;
    this.recipientInfos = paramASN1Set;
    this.encryptedContentInfo = paramEncryptedContentInfo;
    this.unprotectedAttrs = ASN1Set.getInstance(paramAttributes);
  }
  
  public EnvelopedData(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    this.version = (ASN1Integer)paramASN1Sequence.getObjectAt(b++);
    ASN1Encodable aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    if (aSN1Encodable instanceof ASN1TaggedObject) {
      this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)aSN1Encodable, false);
      aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    } 
    this.recipientInfos = ASN1Set.getInstance(aSN1Encodable);
    this.encryptedContentInfo = EncryptedContentInfo.getInstance(paramASN1Sequence.getObjectAt(b++));
    if (paramASN1Sequence.size() > b)
      this.unprotectedAttrs = ASN1Set.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(b), false); 
  }
  
  public static EnvelopedData getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static EnvelopedData getInstance(Object paramObject) {
    return (paramObject instanceof EnvelopedData) ? (EnvelopedData)paramObject : ((paramObject != null) ? new EnvelopedData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public OriginatorInfo getOriginatorInfo() {
    return this.originatorInfo;
  }
  
  public ASN1Set getRecipientInfos() {
    return this.recipientInfos;
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
    if (this.originatorInfo != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo)); 
    aSN1EncodableVector.add((ASN1Encodable)this.recipientInfos);
    aSN1EncodableVector.add((ASN1Encodable)this.encryptedContentInfo);
    if (this.unprotectedAttrs != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.unprotectedAttrs)); 
    return (ASN1Primitive)new BERSequence(aSN1EncodableVector);
  }
  
  public static int calculateVersion(OriginatorInfo paramOriginatorInfo, ASN1Set paramASN1Set1, ASN1Set paramASN1Set2) {
    byte b;
    if (paramOriginatorInfo != null || paramASN1Set2 != null) {
      b = 2;
    } else {
      b = 0;
      Enumeration enumeration = paramASN1Set1.getObjects();
      while (enumeration.hasMoreElements()) {
        RecipientInfo recipientInfo = RecipientInfo.getInstance(enumeration.nextElement());
        if (recipientInfo.getVersion().getValue().intValue() != b) {
          b = 2;
          break;
        } 
      } 
    } 
    return b;
  }
}
